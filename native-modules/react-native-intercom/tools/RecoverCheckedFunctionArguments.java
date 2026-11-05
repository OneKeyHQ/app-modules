import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.lang.model.element.Modifier;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.Type;

/** Restore only explicitly reviewed static argument sites with all-path checked local proofs. */
public final class RecoverCheckedFunctionArguments {
 static final class Unit {CompilationUnitTree tree;SourcePositions positions;String file,owner;ClassTree clazz;Map<String,String> imports=new HashMap<>();}
 static final List<String[]> requests=new ArrayList<>();
 static final Map<String,Unit> units=new HashMap<>();
 static final List<String> edits=new ArrayList<>(),rejections=new ArrayList<>();
 static RememberFunctionalCasts.Context context(Unit unit,MethodTree method){
  var context=new RememberFunctionalCasts.Context();context.unit=unit.tree;context.positions=unit.positions;context.file=unit.file;context.owner=unit.owner;context.imports=unit.imports;context.method=method;return context;
 }
 static boolean signature(RememberFunctionalCasts.Context context,MethodTree method,String descriptor){
  Type[] parameters=Type.getArgumentTypes(descriptor);if(parameters.length!=method.getParameters().size()||!context.type(method.getReturnType(),Type.getReturnType(descriptor)))return false;
  for(int i=0;i<parameters.length;i++)if(!context.type(method.getParameters().get(i).getType(),parameters[i]))return false;return true;
 }
 static void populate(RememberFunctionalCasts.Context context){
  new TreePathScanner<Void,Void>(){
   @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
   @Override public Void visitLambdaExpression(LambdaExpressionTree lambda,Void ignored){return null;}
   @Override public Void visitVariable(VariableTree variable,Void ignored){if(variable.getInitializer()!=null){context.assignments.put(variable,variable);context.rightSides.put(variable,variable.getInitializer());context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(variable);}return super.visitVariable(variable,ignored);}
   @Override public Void visitAssignment(AssignmentTree assignment,Void ignored){if(assignment.getVariable() instanceof IdentifierTree identifier){VariableTree variable=context.binding(identifier);if(variable!=null){context.assignments.put(assignment,variable);context.rightSides.put(assignment,assignment.getExpression());context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(assignment);}}return super.visitAssignment(assignment,ignored);}
   @Override public Void visitCompoundAssignment(CompoundAssignmentTree assignment,Void ignored){if(assignment.getVariable() instanceof IdentifierTree identifier){VariableTree variable=context.binding(identifier);if(variable!=null)context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(assignment);}return super.visitCompoundAssignment(assignment,ignored);}
   @Override public Void visitUnary(UnaryTree unary,Void ignored){if(Set.of(Tree.Kind.PREFIX_INCREMENT,Tree.Kind.POSTFIX_INCREMENT,Tree.Kind.PREFIX_DECREMENT,Tree.Kind.POSTFIX_DECREMENT).contains(unary.getKind())&&unary.getExpression() instanceof IdentifierTree identifier){VariableTree variable=context.binding(identifier);if(variable!=null)context.writes.computeIfAbsent(variable,key->new ArrayList<>()).add(unary);}return super.visitUnary(unary,ignored);}
   @Override public Void visitMethodInvocation(MethodInvocationTree invocation,Void ignored){if(invocation.getMethodSelect() instanceof MemberSelectTree selected&&context.composer(selected.getExpression())){String name=selected.getIdentifier().toString();if(name.equals("startReplaceGroup")&&invocation.getArguments().size()==1){Long key=RememberFunctionalCasts.integer(invocation.getArguments().get(0));context.groups.add(new RememberFunctionalCasts.Group(context.position(invocation),key==null?Long.MIN_VALUE:key,true));}else if(name.equals("endReplaceGroup")&&invocation.getArguments().isEmpty())context.groups.add(new RememberFunctionalCasts.Group(context.position(invocation),0,false));}return super.visitMethodInvocation(invocation,ignored);}
  }.scan(TreePath.getPath(context.unit,context.method.getBody()),null);context.groups.sort(Comparator.comparingLong(group->group.position));
 }
 static boolean staticTarget(String[] row){
  Unit target=units.get(row[3]);if(target==null)return false;List<MethodTree> overloads=new ArrayList<>();for(Tree member:target.clazz.getMembers())if(member instanceof MethodTree method&&method.getName().contentEquals(row[4])&&method.getParameters().size()==Type.getArgumentTypes(row[5]).length)overloads.add(method);
  if(overloads.size()!=1)return false;MethodTree method=overloads.get(0);return method.getModifiers().getFlags().contains(Modifier.STATIC)&&method.getParameters().stream().noneMatch(parameter->parameter.toString().contains("..."))&&signature(context(target,method),method,row[5]);
 }
 public static void main(String[] args)throws Exception {
  Path sourceRoot=Path.of(args[1]);Set<String> files=new TreeSet<>();for(String line:Files.readAllLines(Path.of(args[0]))){String[] row=line.split("\t");requests.add(row);files.add(sourceRoot.resolve(row[0]+".java").toString());files.add(sourceRoot.resolve(row[3]+".java").toString());}
  for(String line:Files.readAllLines(Path.of(args[2]))){String[] row=line.split("\t");RememberFunctionalCasts.originals.computeIfAbsent(String.join("\t",Arrays.copyOf(row,3)),key->new ArrayList<>()).add(row);}
  for(String line:Files.readAllLines(Path.of(args[2]+".hierarchy.tsv"))){String[] row=line.split("\t",-1);RememberFunctionalCasts.hierarchy.put(row[0],row);}
  RememberFunctionalCasts.transparentCheckedCasts.addAll(Files.readAllLines(Path.of(args[3])));
  JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)){
   DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromStrings(files));
   for(CompilationUnitTree tree:task.parse())for(Tree declaration:tree.getTypeDecls())if(declaration instanceof ClassTree clazz){Unit unit=new Unit();unit.tree=tree;unit.clazz=clazz;unit.file=Path.of(tree.getSourceFile().toUri()).toString();unit.owner=tree.getPackageName().toString().replace('.','/')+"/"+clazz.getSimpleName();unit.positions=Trees.instance(task).getSourcePositions();for(ImportTree imported:tree.getImports()){String name=imported.getQualifiedIdentifier().toString();unit.imports.put(name.substring(name.lastIndexOf('.')+1),name);}units.put(unit.owner,unit);}
   if(diagnostics.getDiagnostics().stream().anyMatch(diagnostic->diagnostic.getKind()==Diagnostic.Kind.ERROR))throw new IllegalStateException("Source parse failed");
   for(String[] row:requests){
    if(!staticTarget(row)){rejections.add(row[0]+"\t"+row[1]+"\tactual source target missing/overloaded/nonstatic/signature mismatch");continue;}
    Unit unit=units.get(row[0]);if(unit.clazz.getExtendsClause()!=null){rejections.add(row[0]+"\t"+row[1]+"\tcaller inheritance requires independent field-shadow proof");continue;}List<MethodTree> enclosing=new ArrayList<>();for(Tree member:unit.clazz.getMembers())if(member instanceof MethodTree method&&method.getName().contentEquals(row[1])&&signature(context(unit,method),method,row[2]))enclosing.add(method);
    if(enclosing.size()!=1){rejections.add(row[0]+"\t"+row[1]+"\tenclosing method ambiguous");continue;}
    var context=context(unit,enclosing.get(0));context.descriptor=row[2];populate(context);List<MethodInvocationTree> calls=new ArrayList<>();
    new TreePathScanner<Void,Void>(){
     @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
     @Override public Void visitLambdaExpression(LambdaExpressionTree lambda,Void ignored){return null;}
     @Override public Void visitMethodInvocation(MethodInvocationTree call,Void ignored){
      if(call.getMethodSelect() instanceof MemberSelectTree selected&&selected.getExpression() instanceof IdentifierTree qualifier&&selected.getIdentifier().contentEquals(row[4])&&context.canonical(qualifier.toString()).equals(row[3].replace('/','.').replace('$','.'))&&call.getArguments().size()==Type.getArgumentTypes(row[5]).length){
       boolean field=unit.clazz.getMembers().stream().anyMatch(member->member instanceof VariableTree variable&&variable.getName().contentEquals(qualifier.getName()));if(!field&&SourceCaptureBindings.resolve(getCurrentPath(),qualifier.getName().toString())==null)calls.add(call);
      }return super.visitMethodInvocation(call,ignored);
     }
    }.scan(TreePath.getPath(unit.tree,context.method.getBody()),null);
    MethodInvocationTree call;
    if(calls.size()==1)call=calls.get(0);
    else if(row.length==9&&row[8].equals("REVIEWED_STRAIGHT_LINE_ORDINAL")){
     List<String[]> group=requests.stream().filter(other->Arrays.equals(Arrays.copyOf(other,6),Arrays.copyOf(row,6))).sorted(Comparator.comparingInt(other->Integer.parseInt(other[6]))).toList();
     Set<Integer> offsets=new HashSet<>();boolean invalid=group.size()!=calls.size()||group.stream().anyMatch(other->other.length!=9||!other[8].equals(row[8])||!offsets.add(Integer.parseInt(other[6])));
     for(MethodInvocationTree candidate:calls)for(TreePath path=TreePath.getPath(unit.tree,candidate);path!=null&&path.getLeaf()!=context.method;path=path.getParentPath())if(path.getLeaf() instanceof ForLoopTree||path.getLeaf() instanceof EnhancedForLoopTree||path.getLeaf() instanceof WhileLoopTree||path.getLeaf() instanceof DoWhileLoopTree)invalid=true;
     if(invalid){rejections.add(row[0]+"\t"+row[1]+"\tordered reviewed call inventory mismatch/loop");continue;}
     calls.sort(Comparator.comparingLong(candidate->context.position(candidate)));int ordinal=group.indexOf(row);if(ordinal<0)throw new IllegalStateException("Reviewed ordinal absent");call=calls.get(ordinal);
    }else{rejections.add(row[0]+"\t"+row[1]+"\tsource static call absent/ambiguous/shadowed");continue;}
    Type[] parameters=Type.getArgumentTypes(row[5]);
    for(String position:row[7].split(",")){
     int index=Integer.parseInt(position);Type expected=parameters[index];ExpressionTree argument=call.getArguments().get(index);String rejection=null;List<String> proofs=new ArrayList<>();VariableTree binding=null;
     if(expected.getSort()!=Type.OBJECT||!expected.getInternalName().matches("kotlin/jvm/functions/Function[012]"))rejection="reviewed parameter is not Function0/1/2";
     else if(!(argument instanceof IdentifierTree identifier))rejection="argument is not a bare local";
     else{binding=context.binding(identifier);if(binding==null||binding.getType()==null||!context.canonical(binding.getType().toString()).equals("java.lang.Object"))rejection="argument is not an erased Object local";
      else{Tree statement=call;for(TreePath path=TreePath.getPath(unit.tree,call);path.getParentPath()!=null;path=path.getParentPath()){if(path.getLeaf() instanceof StatementTree&&(path.getParentPath().getLeaf() instanceof BlockTree||path.getParentPath().getLeaf() instanceof CaseTree)){statement=path.getLeaf();break;}if(path.getParentPath().getLeaf() instanceof MethodTree)break;}
       if(context.hasWrite(statement,binding))rejection="same statement writes argument binding";
       else if(!context.checkedLocal(binding,argument,expected.getInternalName(),new HashSet<>(),proofs))rejection="not all reaching paths have checked-value proof";
      }
     }
     if(rejection!=null){rejections.add(String.join("\t",row[0],row[1],""+index,rejection));continue;}
     long start=unit.positions.getStartPosition(unit.tree,argument),end=unit.positions.getEndPosition(unit.tree,argument);edits.add(String.join("\t",unit.file,""+start,""+end,"("+expected.getClassName()+") "+argument,row[0],row[1],row[2],row[3],row[4],row[5],"INVOKESTATIC",""+index,row[6],""+context.position(call),""+unit.positions.getEndPosition(unit.tree,call),argument.toString(),""+unit.tree.getLineMap().getLineNumber(context.position(binding)),expected.getInternalName(),String.join(";",proofs)));
    }
   }
  }Files.write(Path.of(args[4]),edits);Files.write(Path.of(args[4]+".rejected.tsv"),rejections);System.out.println("Proved static checked arguments="+edits.size()+", rejected="+rejections.size());
 }
}
