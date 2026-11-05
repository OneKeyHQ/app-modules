import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.objectweb.asm.Type;

/** Restore only descriptor/group-proved casts at existing typed local cache joins. */
public final class RememberFunctionalCasts {
 static final class Group {long position,key;boolean start;Group(long position,long key,boolean start){this.position=position;this.key=key;this.start=start;}}
 static final class Value {ExpressionTree expression;long position;Value(ExpressionTree expression,long position){this.expression=expression;this.position=position;}}
 static final class Found {int state;boolean incoming;List<Value> values=new ArrayList<>();Found(int state){this.state=state;}Found(ExpressionTree expression,long position){state=1;values.add(new Value(expression,position));}}
 static boolean checkedCopies;
 static final Set<String> transparentCheckedCasts=new HashSet<>();
 static final Map<String,List<String[]>> originals=new HashMap<>();
 static final Map<String,String[]> hierarchy=new HashMap<>();
 static final Map<String,Set<Long>> errors=new HashMap<>();
 static final List<String> edits=new ArrayList<>(),rejected=new ArrayList<>();
 static final Map<String,Integer> statistics=new TreeMap<>();
 static void count(String key){statistics.merge(key,1,Integer::sum);}
 static final class Context {
  CompilationUnitTree unit;MethodTree method;SourcePositions positions;String owner,file,descriptor;
  Map<String,String> imports=new HashMap<>();List<Group> groups=new ArrayList<>();
  Map<Tree,VariableTree> assignments=new IdentityHashMap<>();Map<Tree,ExpressionTree> rightSides=new IdentityHashMap<>();Map<VariableTree,List<Tree>> writes=new IdentityHashMap<>();
  long position(Tree tree){return positions.getStartPosition(unit,tree);}
  String canonical(String text){
   text=text.replaceAll("@[\\w.$]+(?:\\([^)]*\\))?\\s*","").replaceAll("<.*>","").replace(" ","").replace('$','.');
   String suffix="";while(text.endsWith("[]")){suffix+="[]";text=text.substring(0,text.length()-2);}
   for(var imported:imports.entrySet())if(imported.getKey().replace('$','.').equals(text))return imported.getValue().replace('$','.')+suffix;
   String first=text.contains(".")?text.substring(0,text.indexOf('.')):text;
   if(imports.containsKey(first))return imports.get(first).replace('$','.')+text.substring(first.length())+suffix;
   if(text.contains(".")&&Character.isLowerCase(text.charAt(0)))return text+suffix;
   if(Set.of("void","boolean","byte","char","short","int","long","float","double").contains(text))return text+suffix;
   if(first.equals("Object")||first.equals("String")||first.equals("Integer")||first.equals("Boolean")||first.equals("Long")||first.equals("Number")||first.equals("Runnable")||first.equals("CharSequence")||first.equals("Class"))return "java.lang."+text+suffix;
   return unit.getPackageName()+"."+text+suffix;
  }
  boolean type(Tree source,Type expected){return source!=null&&canonical(source.toString()).equals(expected.getClassName().replace('$','.'));}
  String function(Tree source){
   if(source==null)return null;String name=canonical(source.toString());return name.matches("kotlin\\.jvm\\.functions\\.Function[0-9]+")?name.replace('.','/'):null;
  }
  VariableTree binding(IdentifierTree identifier){return SourceCaptureBindings.resolve(TreePath.getPath(unit,identifier),identifier.getName().toString());}
  boolean composer(ExpressionTree expression){
   if(expression instanceof IdentifierTree identifier){VariableTree variable=binding(identifier);return variable!=null&&variable.getType()!=null&&canonical(variable.getType().toString()).equals("androidx.compose.runtime.Composer");}
   if(expression instanceof TypeCastTree cast)return canonical(cast.getType().toString()).equals("androidx.compose.runtime.Composer");return false;
  }
  Group group(long before){Deque<Group> active=new ArrayDeque<>();for(Group group:groups){if(group.position>=before)break;if(group.start)active.push(group);else if(!active.isEmpty())active.pop();}return active.peek();}
  boolean subtype(String type,String expected,Set<String> visited){
   if(type.equals(expected))return true;if(!visited.add(type))return false;String[] parents=hierarchy.get(type);if(parents==null)return false;
   if(!parents[1].isEmpty()&&subtype(parents[1],expected,visited))return true;
   for(String parent:parents[2].split(","))if(!parent.isEmpty()&&subtype(parent,expected,visited))return true;return false;
  }
  boolean hasWrite(Tree statement,VariableTree variable){long start=position(statement),end=positions.getEndPosition(unit,statement);for(Tree write:writes.getOrDefault(variable,List.of()))if(position(write)>=start&&position(write)<=end)return true;return false;}
  Found last(Tree statement,VariableTree variable){
   if(statement instanceof VariableTree declaration&&declaration==variable)return declaration.getInitializer()==null?new Found(2):new Found(declaration.getInitializer(),position(declaration.getInitializer()));
   if(statement instanceof ExpressionStatementTree expression&&assignments.get(expression.getExpression())==variable){ExpressionTree rhs=rightSides.get(expression.getExpression());return new Found(rhs,position(rhs));}
   if(statement instanceof BlockTree block){List<? extends StatementTree> list=block.getStatements();for(int i=list.size()-1;i>=0;i--){Found value=last(list.get(i),variable);if(value.state!=0)return value;}return new Found(0);}
   if(statement instanceof IfTree branch){
    Found yes=last(branch.getThenStatement(),variable),no=branch.getElseStatement()==null?new Found(0):last(branch.getElseStatement(),variable);
    if(yes.state==0&&no.state==0)return new Found(0);
    if(yes.state!=1||no.state!=1)return new Found(2);yes.values.addAll(no.values);return yes;
   }
   return new Found(hasWrite(statement,variable)?2:0);
  }
  Found before(VariableTree variable,Tree use){
   TreePath path=TreePath.getPath(unit,use);Tree child=use;
   for(TreePath parent=path.getParentPath();parent!=null;child=parent.getLeaf(),parent=parent.getParentPath()){
    List<? extends StatementTree> statements=null;
    if(parent.getLeaf()instanceof BlockTree block)statements=block.getStatements();
    else if(parent.getLeaf()instanceof CaseTree branch)statements=branch.getStatements();
    if(statements!=null){int limit=statements.indexOf(child);if(limit<0)continue;for(int i=limit-1;i>=0;i--){Found value=last(statements.get(i),variable);if(value.state!=0)return value;}}
    if(parent.getLeaf()instanceof MethodTree||parent.getLeaf()instanceof ClassTree)break;
   }
   return new Found(2);
  }
  boolean compatible(ExpressionTree expression,String expected,Group cache,long aliasPosition,Set<String> visiting){
   while(expression instanceof ParenthesizedTree parentheses)expression=parentheses.getExpression();
   if(expression instanceof LiteralTree literal)return literal.getValue()==null;
   if(expression instanceof TypeCastTree cast)return expected.equals(function(cast.getType()));
   if(expression instanceof NewClassTree created){String canonical=canonical(created.getIdentifier().toString());for(String candidate:hierarchy.keySet())if(candidate.replace('/','.').replace('$','.').equals(canonical))return subtype(candidate,expected,new HashSet<>());return false;}
   if(expression instanceof MethodInvocationTree invocation&&invocation.getArguments().isEmpty()&&invocation.getMethodSelect()instanceof MemberSelectTree selected&&selected.getIdentifier().contentEquals("rememberedValue")&&selected.getExpression()instanceof IdentifierTree receiver){
    VariableTree local=binding(receiver);Group actual=group(position(expression));return local!=null&&local.getType()!=null&&canonical(local.getType().toString()).equals("androidx.compose.runtime.Composer")&&actual!=null&&actual.position==cache.position&&position(expression)<aliasPosition;
   }
   if(!(expression instanceof IdentifierTree identifier))return false;
   VariableTree local=binding(identifier);if(local==null||local.getType()==null)return false;if(expected.equals(function(local.getType())))return true;
   if(local.getType()instanceof PrimitiveTypeTree)return false;
   String key=System.identityHashCode(local)+":"+position(expression);if(!visiting.add(key))return false;
   Found definitions=before(local,expression);boolean good=definitions.state==1&&!definitions.values.isEmpty();
   if(good)for(Value value:definitions.values)if(!compatible(value.expression,expected,cache,aliasPosition,visiting)){good=false;break;}
   visiting.remove(key);return good;
  }
  boolean nextEnd(Tree statement){
   TreePath path=TreePath.getPath(unit,statement);Tree parent=path.getParentPath().getLeaf();List<? extends StatementTree> list=parent instanceof BlockTree block?block.getStatements():parent instanceof CaseTree branch?branch.getStatements():null;
   if(list==null)return false;int index=list.indexOf(statement);if(index<0||index+1>=list.size()||!(list.get(index+1)instanceof ExpressionStatementTree following)||!(following.getExpression()instanceof MethodInvocationTree invocation)||!invocation.getArguments().isEmpty()||!(invocation.getMethodSelect()instanceof MemberSelectTree selected))return false;
   return selected.getIdentifier().contentEquals("endReplaceGroup")&&composer(selected.getExpression());
  }
  Found mergePaths(Found yes,Found no){
   if(yes.state==2||no.state==2)return new Found(2);if(yes.state==0&&no.state==0)return new Found(0);
   Found result=new Found(1);result.values.addAll(yes.values);result.values.addAll(no.values);result.incoming=yes.state==0||no.state==0||yes.incoming||no.incoming;return result;
  }
  Found checkedLast(Tree statement,VariableTree variable){
   if(statement instanceof VariableTree declaration&&declaration==variable)return declaration.getInitializer()==null?new Found(2):new Found(declaration.getInitializer(),position(declaration.getInitializer()));
   if(statement instanceof ExpressionStatementTree expression&&assignments.get(expression.getExpression())==variable){ExpressionTree rhs=rightSides.get(expression.getExpression());return rhs==null?new Found(2):new Found(rhs,position(rhs));}
   if(statement instanceof BlockTree block)return checkedPrevious(block.getStatements(),block.getStatements().size(),variable,new Found(0));
   if(statement instanceof IfTree branch){if(hasWrite(branch.getCondition(),variable))return new Found(2);return mergePaths(checkedLast(branch.getThenStatement(),variable),branch.getElseStatement()==null?new Found(0):checkedLast(branch.getElseStatement(),variable));}
   return new Found(hasWrite(statement,variable)?2:0);
  }
  Found checkedPrevious(List<? extends StatementTree> statements,int limit,VariableTree variable,Found pending){
   for(int index=limit-1;index>=0;index--){Found previous=checkedLast(statements.get(index),variable);if(previous.state==0)continue;if(previous.state==2)return previous;
    if(pending.state==0)pending=previous;else{pending.values.addAll(previous.values);pending.incoming=previous.incoming;}
    if(!pending.incoming)return pending;
   }return pending;
  }
  Found checkedBefore(VariableTree variable,Tree use){
   Found pending=new Found(0);Tree child=use;
   for(TreePath parent=TreePath.getPath(unit,use).getParentPath();parent!=null;child=parent.getLeaf(),parent=parent.getParentPath()){
    Tree scope=parent.getLeaf();List<? extends StatementTree> statements=scope instanceof BlockTree block?block.getStatements():scope instanceof CaseTree branch?branch.getStatements():null;
    if(statements!=null){int limit=statements.indexOf(child);if(limit>=0){pending=checkedPrevious(statements,limit,variable,pending);if(pending.state==2||pending.state==1&&!pending.incoming)return pending;}}
    if(scope instanceof IfTree branch&&hasWrite(branch.getCondition(),variable))return new Found(2);
    if((scope instanceof ForLoopTree||scope instanceof EnhancedForLoopTree||scope instanceof WhileLoopTree||scope instanceof DoWhileLoopTree||scope instanceof TryTree||scope instanceof SwitchTree)&&hasWrite(scope,variable))return new Found(2);
    if(scope instanceof MethodTree||scope instanceof ClassTree)break;
   }return pending;
  }
  boolean checkedLocal(VariableTree local,Tree use,String expected,Set<String> visiting,List<String> proofs){
   if(local==null||local.getType()==null||local.getType() instanceof PrimitiveTypeTree)return false;
   String key=System.identityHashCode(local)+":"+position(use);if(!visiting.add(key))return false;Found definitions=checkedBefore(local,use);boolean good=definitions.state!=2;
   if(good&&(definitions.state==0||definitions.incoming)){
    int index=method.getParameters().indexOf(local);Type[] parameters=Type.getArgumentTypes(descriptor);good=index>=0&&expected.equals(function(local.getType()))&&parameters[index].getSort()==Type.OBJECT&&parameters[index].getInternalName().equals(expected);
    if(good)proofs.add("PARAMETER@"+index+":"+parameters[index].getDescriptor());
   }
   if(good)for(Value value:definitions.values)if(!checkedExpression(value.expression,expected,visiting,proofs)){good=false;break;}
   visiting.remove(key);return good;
  }
  boolean checkedExpression(ExpressionTree expression,String expected,Set<String> visiting,List<String> proofs){
   while(expression instanceof ParenthesizedTree parenthesized)expression=parenthesized.getExpression();
   if(expression instanceof LiteralTree literal&&literal.getValue()==null){proofs.add("NULL@"+position(expression));return true;}
   if(expression instanceof TypeCastTree cast){
    if(cast.getExpression() instanceof IdentifierTree identifier&&transparentCheckedCasts.contains(file+"\t"+unit.getLineMap().getLineNumber(position(cast))+"\t"+expected+"\t"+identifier.getName())){proofs.add("RETRACE_PRIOR_RECOVERED_CAST@"+unit.getLineMap().getLineNumber(position(cast))+":"+identifier.getName());return checkedExpression(identifier,expected,visiting,proofs);}
    if(!expected.equals(function(cast.getType())))return false;Group cache=group(position(cast));if(cache==null||cache.key==Long.MIN_VALUE)return false;
    List<String[]> joins=new ArrayList<>();for(String[] row:originals.getOrDefault(owner+"\t"+method.getName()+"\t"+descriptor,List.of()))if(row[3].equals(""+cache.key)&&row[4].equals(expected))joins.add(row);
    if(joins.size()!=1)return false;String[] original=joins.get(0);proofs.add("EXISTING_CAST@"+position(cast)+":GROUP="+cache.key+":REFERENCE_BCI="+original[5]+":STORE="+original[6]);return true;
   }
   if(expression instanceof IdentifierTree identifier)return checkedLocal(binding(identifier),identifier,expected,visiting,proofs);
   if(expression instanceof NewClassTree created){String type=canonical(created.getIdentifier().toString());List<String> candidates=new ArrayList<>();for(String candidate:hierarchy.keySet())if(candidate.replace('/','.').replace('$','.').equals(type))candidates.add(candidate);if(candidates.size()!=1||!subtype(candidates.get(0),expected,new HashSet<>()))return false;proofs.add("NEW_ORIGINAL_SUBTYPE@"+position(created)+":"+candidates.get(0));return true;}
   return false;
  }
  void checkedCandidate(Tree statement,ExpressionTree rhs,Tree targetType){
   if(!(rhs instanceof IdentifierTree identifier))return;long line=unit.getLineMap().getLineNumber(position(rhs));if(!errors.getOrDefault(file,Set.of()).contains(line))return;count("current_diagnostic_typed_sites");String expected=function(targetType);List<String> proofs=new ArrayList<>();boolean good=expected!=null&&checkedLocal(binding(identifier),identifier,expected,new HashSet<>(),proofs);
   if(!good){count("not all reaching values have an existing checked/parameter/subtype proof");rejected.add(String.join("\t",file,""+line,owner,method.getName().toString(),descriptor,"unproved checked-value copy"));return;}
   edits.add(String.join("\t",file,""+position(rhs),""+positions.getEndPosition(unit,rhs),"("+expected.replace('/','.')+") "+rhs,owner,method.getName().toString(),descriptor,expected,""+line,String.join(";",proofs)));count("proved_checked_value_copies");
  }
  void candidate(Tree statement,ExpressionTree rhs,Tree targetType){
   if(checkedCopies){checkedCandidate(statement,rhs,targetType);return;}
   if(!(rhs instanceof IdentifierTree identifier))return;
   long line=unit.getLineMap().getLineNumber(position(rhs));if(!errors.getOrDefault(file,Set.of()).contains(line))return;
   count("current_diagnostic_typed_sites");String expected=function(targetType);String reason=null;Group cache=group(position(rhs));
   if(expected==null)reason="target is not import-resolved Kotlin FunctionN";
   else if(binding(identifier)==null)reason="RHS is not a lexical local";
   else if(cache==null||cache.key==Long.MIN_VALUE)reason="no literal active source Composer cache group";
   else if(!nextEnd(statement))reason="typed alias is not immediately before source group end";
   List<String[]> joins=new ArrayList<>();
   if(reason==null)for(String[] row:originals.getOrDefault(owner+"\t"+method.getName()+"\t"+descriptor,List.of()))if(row[3].equals(""+cache.key)&&row[4].equals(expected))joins.add(row);
   if(reason==null&&joins.size()!=1)reason="original descriptor/group/CHECKCAST join is absent or ambiguous";
   if(reason==null&&!compatible(rhs,expected,cache,position(rhs),new HashSet<>()))reason="reaching producers are mixed, primitive, nonlocal or unproved";
   if(reason!=null){count(reason);rejected.add(String.join("\t",file,""+line,owner,method.getName().toString(),descriptor,reason));return;}
   String[] proof=joins.get(0);edits.add(String.join("\t",file,""+position(rhs),""+positions.getEndPosition(unit,rhs),"("+expected.replace('/','.')+") "+rhs,owner,method.getName().toString(),descriptor,proof[3],proof[4],proof[5],proof[6],""+line));count("proved_source_casts");
  }
 }
 static Long integer(ExpressionTree expression){if(expression instanceof LiteralTree literal&&literal.getValue()instanceof Number number)return number.longValue();if(expression instanceof UnaryTree unary&&unary.getKind()==Tree.Kind.UNARY_MINUS){Long value=integer(unary.getExpression());return value==null?null:-value;}return null;}
 public static void main(String[] args)throws Exception {
  checkedCopies=args.length>4&&args[4].equals("--checked-copies");
  for(String line:Files.readAllLines(Path.of(args[0]))){String[] row=line.split("\t");originals.computeIfAbsent(String.join("\t",Arrays.copyOf(row,3)),ignored->new ArrayList<>()).add(row);}
  for(String line:Files.readAllLines(Path.of(args[0]+".hierarchy.tsv"))){String[] row=line.split("\t",-1);hierarchy.put(row[0],row);}
  Pattern diagnostic=Pattern.compile("^(.*\\.java):(\\d+): error: incompatible types: Object cannot be converted to .*Function[0-9]+$");
  for(String line:Files.readAllLines(Path.of(args[2]))){Matcher match=diagnostic.matcher(line);if(match.matches())errors.computeIfAbsent(match.group(1),ignored->new HashSet<>()).add(Long.parseLong(match.group(2)));}
  JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();List<java.io.File> files=new ArrayList<>();for(String file:errors.keySet())files.add(new java.io.File(file));
  try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)){
   DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
   for(CompilationUnitTree unit:task.parse()){
    SourcePositions positions=Trees.instance(task).getSourcePositions();Map<String,String> imports=new HashMap<>();for(ImportTree imported:unit.getImports()){String value=imported.getQualifiedIdentifier().toString();imports.put(value.substring(value.lastIndexOf('.')+1),value);}
    new TreePathScanner<Void,Void>(){
     @Override public Void visitMethod(MethodTree method,Void unused){
      if(method.getBody()==null||!(getCurrentPath().getParentPath().getLeaf()instanceof ClassTree clazz))return null;
      Context context=new Context();context.unit=unit;context.positions=positions;context.method=method;context.owner=unit.getPackageName().toString().replace('.','/')+"/"+clazz.getSimpleName();context.file=Path.of(unit.getSourceFile().toUri()).toString();context.imports=imports;
      Set<String> descriptors=new HashSet<>();for(String key:originals.keySet())if(key.startsWith(context.owner+"\t"+method.getName()+"\t"))descriptors.add(key.substring(key.lastIndexOf('\t')+1));
      List<String> matching=new ArrayList<>();for(String descriptor:descriptors){Type[] parameters=Type.getArgumentTypes(descriptor);if(parameters.length!=method.getParameters().size()||!context.type(method.getReturnType(),Type.getReturnType(descriptor)))continue;boolean same=true;for(int i=0;i<parameters.length;i++)if(!context.type(method.getParameters().get(i).getType(),parameters[i]))same=false;if(same)matching.add(descriptor);}
      if(matching.size()!=1)return null;context.descriptor=matching.get(0);
      new TreePathScanner<Void,Void>(){
       @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
       @Override public Void visitLambdaExpression(LambdaExpressionTree lambda,Void ignored){return null;}
       @Override public Void visitVariable(VariableTree variable,Void ignored){if(variable.getInitializer()!=null){context.assignments.put(variable,variable);context.rightSides.put(variable,variable.getInitializer());context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(variable);}return super.visitVariable(variable,ignored);}
       @Override public Void visitAssignment(AssignmentTree assignment,Void ignored){if(assignment.getVariable()instanceof IdentifierTree identifier){VariableTree variable=context.binding(identifier);if(variable!=null){context.assignments.put(assignment,variable);context.rightSides.put(assignment,assignment.getExpression());context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(assignment);}}return super.visitAssignment(assignment,ignored);}
       @Override public Void visitCompoundAssignment(CompoundAssignmentTree assignment,Void ignored){if(assignment.getVariable() instanceof IdentifierTree identifier){VariableTree variable=context.binding(identifier);if(variable!=null)context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(assignment);}return super.visitCompoundAssignment(assignment,ignored);}
       @Override public Void visitUnary(UnaryTree unary,Void ignored){if(Set.of(Tree.Kind.PREFIX_INCREMENT,Tree.Kind.POSTFIX_INCREMENT,Tree.Kind.PREFIX_DECREMENT,Tree.Kind.POSTFIX_DECREMENT).contains(unary.getKind())&&unary.getExpression() instanceof IdentifierTree identifier){VariableTree variable=context.binding(identifier);if(variable!=null)context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(unary);}return super.visitUnary(unary,ignored);}
       @Override public Void visitMethodInvocation(MethodInvocationTree invocation,Void ignored){if(invocation.getMethodSelect()instanceof MemberSelectTree selected&&context.composer(selected.getExpression())){String name=selected.getIdentifier().toString();if(name.equals("startReplaceGroup")&&invocation.getArguments().size()==1){Long key=integer(invocation.getArguments().get(0));context.groups.add(new Group(context.position(invocation),key==null?Long.MIN_VALUE:key,true));}else if(name.equals("endReplaceGroup")&&invocation.getArguments().isEmpty())context.groups.add(new Group(context.position(invocation),0,false));}return super.visitMethodInvocation(invocation,ignored);}
      }.scan(TreePath.getPath(unit,method.getBody()),null);
      context.groups.sort(Comparator.comparingLong(group->group.position));
      new TreePathScanner<Void,Void>(){
       @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
       @Override public Void visitLambdaExpression(LambdaExpressionTree lambda,Void ignored){return null;}
       @Override public Void visitVariable(VariableTree variable,Void ignored){if(variable.getInitializer()!=null)context.candidate(variable,variable.getInitializer(),variable.getType());return super.visitVariable(variable,ignored);}
       @Override public Void visitExpressionStatement(ExpressionStatementTree statement,Void ignored){if(statement.getExpression()instanceof AssignmentTree assignment&&assignment.getVariable()instanceof IdentifierTree identifier){VariableTree target=context.binding(identifier);if(target!=null)context.candidate(statement,assignment.getExpression(),target.getType());}return super.visitExpressionStatement(statement,ignored);}
      }.scan(TreePath.getPath(unit,method.getBody()),null);
      return null;
     }
    }.scan(unit,null);
   }
   long parseErrors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();if(parseErrors!=0)throw new IllegalStateException("Parse errors: "+parseErrors);
  }
  Collections.sort(edits);Collections.sort(rejected);Files.write(Path.of(args[3]),edits);Files.write(Path.of(args[3]+".rejected.tsv"),rejected);System.out.println(statistics);
 }
}
