import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.Type;

/** Recover bounded direct-local static factory expressions from descriptor/SSA evidence. */
public final class RecoverStaticCaptureExpressions {
 static final List<String[]> requests=new ArrayList<>();
 static final List<String> edits=new ArrayList<>(),rejections=new ArrayList<>();
 static final Set<String> owned=new HashSet<>();
 static boolean expressionFactories;
 static String javaType(String descriptor){
  Type type=Type.getType(descriptor);if(type.getSort()==Type.ARRAY)return javaType(type.getElementType().getDescriptor())+"[]".repeat(type.getDimensions());
  return type.getSort()==Type.OBJECT?(owned.contains(type.getInternalName())?type.getClassName():type.getClassName().replace('$','.')):type.getClassName();
 }
 static String adapt(String descriptor,String argument){
  Type type=Type.getType(descriptor);if(type.getSort()==Type.OBJECT||type.getSort()==Type.ARRAY)return "("+javaType(descriptor)+")"+argument;
  String[] pair=switch(descriptor){case "Z"->new String[]{"Boolean","booleanValue"};case "B"->new String[]{"Byte","byteValue"};case "C"->new String[]{"Character","charValue"};case "S"->new String[]{"Short","shortValue"};case "I"->new String[]{"Integer","intValue"};case "J"->new String[]{"Long","longValue"};case "F"->new String[]{"Float","floatValue"};case "D"->new String[]{"Double","doubleValue"};default->throw new IllegalArgumentException(descriptor);};
  return "((java.lang."+pair[0]+")"+argument+")."+pair[1]+"()";
 }
 static final class Context {
  CompilationUnitTree unit;SourcePositions positions;MethodTree method;ClassTree clazz;String source,owner,file;Map<String,String> imports=new HashMap<>();Set<String> declaredFields=new HashSet<>();
  long start(Tree tree){return positions.getStartPosition(unit,tree);}long end(Tree tree){return positions.getEndPosition(unit,tree);}
  String canonical(String text){
   text=text.replaceAll("@[\\w.$]+(?:\\([^)]*\\))?\\s*","").replaceAll("<.*>","").replace(" ","").replace('$','.');String suffix="";while(text.endsWith("[]")){suffix+="[]";text=text.substring(0,text.length()-2);}
   String raw=text.replace('.','$');for(var imported:imports.entrySet())if(imported.getKey().replace('$','.').equals(text))return imported.getValue().replace('$','.')+suffix;
   String first=text.contains(".")?text.substring(0,text.indexOf('.')):text;if(imports.containsKey(first))return imports.get(first).replace('$','.')+text.substring(first.length())+suffix;
   if(text.contains(".")&&Character.isLowerCase(text.charAt(0)))return text+suffix;
   if(Set.of("void","boolean","byte","char","short","int","long","float","double").contains(text))return text+suffix;
   if(Set.of("Object","String","Integer","Boolean","Long","Number","Throwable","Byte","Character","Short","Float","Double").contains(first))return "java.lang."+text+suffix;
   return unit.getPackageName()+"."+text+suffix;
  }
  boolean type(Tree tree,Type type){return tree!=null&&canonical(tree.toString()).equals(type.getClassName().replace('$','.'));}
  boolean method(String descriptor){Type[] parameters=Type.getArgumentTypes(descriptor);if(parameters.length!=method.getParameters().size())return false;if(method.getReturnType()!=null&&!type(method.getReturnType(),Type.getReturnType(descriptor)))return false;for(int i=0;i<parameters.length;i++)if(!type(method.getParameters().get(i).getType(),parameters[i]))return false;return true;}
  String reject(String[] row,String reason){rejections.add(row[0]+"\t"+file+"\t"+reason);return reason;}
  boolean pureRead(ExpressionTree expression,TreePath at,boolean allowClassQualifier){
   if(expression instanceof LiteralTree)return true;
   if(expression instanceof ParenthesizedTree parenthesized)return pureRead(parenthesized.getExpression(),at,allowClassQualifier);
   if(expression instanceof IdentifierTree identifier){String name=identifier.getName().toString();return name.equals("this")||SourceCaptureBindings.resolve(at,name)!=null||allowClassQualifier&&!declaredFields.contains(name)&&(imports.containsKey(name)||owned.stream().anyMatch(type->type.replace('/','.').replace('$','.').equals(canonical(name))));}
   if(allowClassQualifier&&expression instanceof MemberSelectTree selected){String text=selected.toString();String first=text.substring(0,text.indexOf('.'));return !first.equals("this")&&!declaredFields.contains(first)&&SourceCaptureBindings.resolve(at,first)==null&&(imports.containsValue(text)||owned.stream().anyMatch(type->type.replace('/','.').replace('$','.').equals(canonical(text))));}
   return false;
  }
  boolean fieldEvaluationPrefix(TreePath path,StatementTree anchor){
   Tree child=path.getLeaf();for(TreePath parent=path.getParentPath();parent!=null;child=parent.getLeaf(),parent=parent.getParentPath()){
    Tree tree=parent.getLeaf();if(tree==anchor){
     if(tree instanceof VariableTree variable)return variable.getInitializer()==child;
     if(tree instanceof ExpressionStatementTree statement)return statement.getExpression()==child;
     if(tree instanceof ReturnTree returned)return returned.getExpression()==child;
     if(tree instanceof ThrowTree thrown)return thrown.getExpression()==child;return false;
    }
    if(tree instanceof ParenthesizedTree||tree instanceof TypeCastTree)continue;
    if(tree instanceof AssignmentTree assigned){if(assigned.getExpression()!=child)return false;ExpressionTree left=assigned.getVariable();if(left instanceof IdentifierTree)continue;if(left instanceof MemberSelectTree selected&&pureRead(selected.getExpression(),parent,false))continue;return false;}
    if(tree instanceof MethodInvocationTree invocation){
     int index=invocation.getArguments().indexOf(child);if(index<0)return false;
     if(invocation.getMethodSelect() instanceof MemberSelectTree selected&&!pureRead(selected.getExpression(),parent,true))return false;
     for(int i=0;i<index;i++)if(!pureRead(invocation.getArguments().get(i),parent,false))return false;continue;
    }
    return false;
   }return false;
  }
  void recover(String[] row,MemberReferenceTree reference,TreePath path){
   boolean fieldInitializer=method==null;TreePath cursor=path;StatementTree anchor=null;
   while(cursor.getParentPath()!=null){Tree parent=cursor.getParentPath().getLeaf();if(cursor.getLeaf()instanceof StatementTree statement&&(parent instanceof BlockTree||parent instanceof CaseTree||fieldInitializer&&statement instanceof VariableTree&&parent instanceof ClassTree)){anchor=statement;break;}if(parent instanceof MethodTree||parent instanceof ClassTree||parent instanceof LambdaExpressionTree)break;cursor=cursor.getParentPath();}
   if(!(anchor instanceof VariableTree||anchor instanceof ExpressionStatementTree||anchor instanceof ReturnTree||anchor instanceof ThrowTree)){reject(row,"factory has no direct bounded block statement anchor");return;}
   if(method!=null&&source.substring((int)start(method),(int)end(method)).contains("recoveredThis")&&row[11].contains("|this|")){reject(row,"receiver local was independently rewritten");return;}
   String[] captureStrings=row[11].isEmpty()?new String[0]:row[11].split(",");List<String> arguments=new ArrayList<>(),declarations=new ArrayList<>(),proofs=new ArrayList<>(),captureValues=new ArrayList<>(),factoryParameters=new ArrayList<>();Set<VariableTree> bindings=Collections.newSetFromMap(new IdentityHashMap<>());
   boolean fields=Arrays.stream(captureStrings).anyMatch(capture->capture.startsWith("FIELD|"));
   boolean factory=fields&&!fieldEvaluationPrefix(path,anchor);
   if(factory&&!expressionFactories){reject(row,"field capture factory has earlier effectful/throwing expression or conditional evaluation");return;}
   for(int i=0;i<captureStrings.length;i++){
    String[] capture=captureStrings[i].split("\\|",-1);String descriptor=capture[5],name=capture[4],identifier="recoveredExpressionCapture_"+row[0]+"_"+i;
    boolean field=capture[0].equals("FIELD");
    if(!capture[0].equals("LOCAL")&&!field){reject(row,"nonlocal capture requires original field producer evidence");return;}
    if(fieldInitializer){if(!name.equals("this")||!descriptor.equals("L"+owner+";")){reject(row,"field initializer capture is not stable current this");return;}arguments.add("this");proofs.add("STABLE_FIELD_INITIALIZER_THIS:"+captureStrings[i]);continue;}
    if(!name.equals("this")){
     VariableTree binding=SourceCaptureBindings.resolve(path,name);if(binding==null||binding.getType()==null){reject(row,"capture lexical binding is absent or inferred");return;}
     String bindingDescriptor=field?capture[8]:descriptor;
     if(Type.getType(bindingDescriptor).getSort()!=Type.OBJECT&&Type.getType(bindingDescriptor).getSort()!=Type.ARRAY&&!type(binding.getType(),Type.getType(bindingDescriptor))){reject(row,"primitive capture source binding disagrees with original descriptor");return;}
     if(Type.getType(bindingDescriptor).getSort()>=Type.ARRAY&&binding.getType() instanceof PrimitiveTypeTree){reject(row,"reference capture source was inferred as primitive");return;}
     if(start(binding)>=start(anchor)){reject(row,"capture declared inside enclosing expression");return;}bindings.add(binding);
     proofs.add(identifier+"="+name+"@"+start(binding)+":"+captureStrings[i]);
    }else proofs.add(identifier+"=this:"+captureStrings[i]);
    String value=field?"(("+javaType("L"+capture[6]+";")+") "+name+")."+capture[7]:name;
    declarations.add("final "+javaType(descriptor)+" "+identifier+" = "+value+";");arguments.add(identifier);captureValues.add(value);factoryParameters.add(javaType(descriptor)+" "+identifier);
   }
   final boolean[] changed={false};
   new TreePathScanner<Void,Void>(){
    void write(ExpressionTree expression){if(expression instanceof IdentifierTree identifier){VariableTree binding=SourceCaptureBindings.resolve(getCurrentPath(),identifier.getName().toString());if(bindings.contains(binding))changed[0]=true;}}
    @Override public Void visitAssignment(AssignmentTree assignment,Void unused){write(assignment.getVariable());return super.visitAssignment(assignment,unused);}
    @Override public Void visitCompoundAssignment(CompoundAssignmentTree assignment,Void unused){write(assignment.getVariable());return super.visitCompoundAssignment(assignment,unused);}
    @Override public Void visitUnary(UnaryTree unary,Void unused){if(Set.of(Tree.Kind.PREFIX_INCREMENT,Tree.Kind.POSTFIX_INCREMENT,Tree.Kind.PREFIX_DECREMENT,Tree.Kind.POSTFIX_DECREMENT).contains(unary.getKind()))write(unary.getExpression());return super.visitUnary(unary,unused);}
    @Override public Void visitClass(ClassTree nested,Void unused){return null;}
    @Override public Void visitLambdaExpression(LambdaExpressionTree nested,Void unused){return null;}
   }.scan(TreePath.getPath(unit,anchor),null);
   if(changed[0]&&!factory){reject(row,"enclosing expression writes a captured lexical binding");return;}
   Type[] target=Type.getArgumentTypes(row[7]),sam=Type.getArgumentTypes(row[9]),instantiated=Type.getArgumentTypes(row[10]);if(target.length!=captureStrings.length+sam.length||sam.length!=instantiated.length){reject(row,"target and erased/instantiated SAM arity disagree");return;}
   for(int i=0;i<captureStrings.length;i++)if(!Type.getType(captureStrings[i].split("\\|",-1)[5]).equals(target[i])){reject(row,"captured descriptor differs from target leading parameter");return;}
   List<String> samNames=new ArrayList<>();for(int i=0;i<sam.length;i++){
    String name="recoveredExpressionArgument_"+row[0]+"_"+i;samNames.add(name);Type expected=target[captureStrings.length+i],erased=sam[i],instance=instantiated[i];
    if(expected.getSort()<Type.ARRAY){
     Map<String,String> wrappers=Map.of("Z","java/lang/Boolean","B","java/lang/Byte","C","java/lang/Character","S","java/lang/Short","I","java/lang/Integer","J","java/lang/Long","F","java/lang/Float","D","java/lang/Double");
     if(!instance.equals(expected)&&!(instance.getSort()==Type.OBJECT&&instance.getInternalName().equals(wrappers.get(expected.getDescriptor())))){reject(row,"primitive adaptation is not exact instantiated wrapper");return;}
     arguments.add(erased.equals(expected)?name:adapt(expected.getDescriptor(),name));
    }else{
     if(instance.getSort()<Type.ARRAY){reject(row,"primitive instantiated value to reference target requires separate boxing proof");return;}
     String adapted=erased.equals(instance)?name:adapt(instance.getDescriptor(),name);arguments.add("("+javaType(expected.getDescriptor())+") ("+adapted+")");
    }
   }
   String call=javaType("L"+row[5]+";")+"."+row[6]+"("+String.join(", ",arguments)+")";
   String expression="(("+javaType("L"+row[8]+";")+") ("+String.join(", ",samNames)+") -> "+call+")";
   String helper="",helperDescriptor="",helperName="";long helperPosition=-1;
   if(factory){
    helperName="recoveredExpressionFactory_"+row[0];helperDescriptor="("+String.join("",Arrays.stream(captureStrings).map(capture->capture.split("\\|",-1)[5]).toList())+")L"+row[8]+";";
    helper="\n   private static "+javaType("L"+row[8]+";")+" "+helperName+"("+String.join(", ",factoryParameters)+") {\n      return "+expression+";\n   }\n";
    helperPosition=end(clazz)-1;expression=javaType("L"+owner+";")+"."+helperName+"("+String.join(", ",captureValues)+")";declarations.clear();
   }
   String prefix=source.substring(source.lastIndexOf('\n',(int)start(anchor))+1,(int)start(anchor));if(!prefix.isBlank()){reject(row,"statement is not at a direct line indentation boundary");return;}
   String snapshots=declarations.isEmpty()?"":String.join("\n"+prefix,declarations)+"\n"+prefix;
   edits.add(String.join("\t",file,""+start(reference),""+end(reference),expression,""+start(anchor),snapshots.replace("\n","\\n"),row[0],row[3],row[12],String.join(";",proofs),fieldInitializer?"FIELD:"+((VariableTree)anchor).getName():factory?"EXPRESSION_FACTORY":"BLOCK",""+helperPosition,helper.replace("\n","\\n"),helperName,helperDescriptor));
  }
 }
 public static void main(String[] args)throws Exception {
  expressionFactories=args.length>3&&args[3].equals("--expression-factories");
  Path sourceRoot=Path.of(args[1]);Path ownedRoot=args.length>4?Path.of(args[4]):sourceRoot;try(var paths=Files.walk(ownedRoot)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->owned.add(ownedRoot.relativize(path).toString().replace(java.io.File.separatorChar,'/').replaceFirst("\\.java$","")));}
  Set<String> files=new TreeSet<>();for(String line:Files.readAllLines(Path.of(args[0]))){String[] row=line.split("\t",-1);requests.add(row);files.add(sourceRoot.resolve(row[1]+".java").toString());}
  JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)){
   DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromStrings(files));
   for(CompilationUnitTree unit:task.parse()){
    Context context=new Context();context.unit=unit;context.positions=Trees.instance(task).getSourcePositions();context.file=Path.of(unit.getSourceFile().toUri()).toString();context.source=Files.readString(Path.of(context.file));context.owner=sourceRoot.relativize(Path.of(context.file)).toString().replace(java.io.File.separatorChar,'/').replaceFirst("\\.java$","");
    for(ImportTree imported:unit.getImports())if(!imported.isStatic()){String name=imported.getQualifiedIdentifier().toString();context.imports.put(name.substring(name.lastIndexOf('.')+1),name);}
    new TreePathScanner<Void,Void>(){
     @Override public Void visitVariable(VariableTree variable,Void unused){
      if(variable.getInitializer()==null||!(getCurrentPath().getParentPath().getLeaf() instanceof ClassTree clazz)||!clazz.getSimpleName().contentEquals(context.owner.substring(context.owner.lastIndexOf('/')+1)))return super.visitVariable(variable,unused);
      context.method=null;
      for(String[] row:requests)if(row[1].equals(context.owner)&&row[2].equals("<init>")&&row[3].equals("()V")){
       List<TreePath> matches=new ArrayList<>();new TreePathScanner<Void,Void>(){
        @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
        @Override public Void visitLambdaExpression(LambdaExpressionTree nested,Void ignored){return null;}
        @Override public Void visitMemberReference(MemberReferenceTree reference,Void ignored){if(reference.getName().contentEquals(row[6])&&context.canonical(reference.getQualifierExpression().toString()).equals(row[5].replace('/','.').replace('$','.')))matches.add(getCurrentPath());return null;}
       }.scan(TreePath.getPath(unit,variable.getInitializer()),null);
       if(matches.size()==1)context.recover(row,(MemberReferenceTree)matches.get(0).getLeaf(),matches.get(0));else if(matches.size()>1)context.reject(row,"field initializer reference is ambiguous");
      }return null;
     }
     @Override public Void visitMethod(MethodTree method,Void unused){
      if(method.getBody()==null||!(getCurrentPath().getParentPath().getLeaf() instanceof ClassTree clazz)||!clazz.getSimpleName().contentEquals(context.owner.substring(context.owner.lastIndexOf('/')+1)))return null;context.method=method;context.clazz=clazz;context.declaredFields.clear();for(Tree member:clazz.getMembers())if(member instanceof VariableTree field)context.declaredFields.add(field.getName().toString());
      for(String[] row:requests)if(row[1].equals(context.owner)&&row[2].equals(method.getName().toString())&&context.method(row[3])){
       List<TreePath> matches=new ArrayList<>();new TreePathScanner<Void,Void>(){
        @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
        @Override public Void visitLambdaExpression(LambdaExpressionTree nested,Void ignored){return null;}
        @Override public Void visitMemberReference(MemberReferenceTree reference,Void ignored){if(reference.getName().contentEquals(row[6])&&context.canonical(reference.getQualifierExpression().toString()).equals(row[5].replace('/','.').replace('$','.')))matches.add(getCurrentPath());return null;}
       }.scan(TreePath.getPath(unit,method.getBody()),null);
       if(matches.size()!=1)context.reject(row,"method-scoped reference is absent or ambiguous: "+matches.size());else context.recover(row,(MemberReferenceTree)matches.get(0).getLeaf(),matches.get(0));
      }return null;
     }
    }.scan(unit,null);
   }
   if(diagnostics.getDiagnostics().stream().anyMatch(diagnostic->diagnostic.getKind()==Diagnostic.Kind.ERROR))throw new IllegalStateException("Source parse failed");
  }
  Files.write(Path.of(args[2]),edits);Files.write(Path.of(args[2]+".rejected.tsv"),rejections);System.out.println("Recovered direct-local expression sites "+edits.size()+"; rejected "+rejections.size());
 }
}
