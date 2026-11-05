import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;

/** Correct only exact original owned type references for top-level recovered binary-name source files. */
public final class NormalizeOwnedTypeReferences {
 static final Map<String,String> canonicalToBinary=new HashMap<>();
 static final Map<String,Set<String>> canonicalCandidates=new HashMap<>();
 static final Map<String,Set<String>> hierarchyFields=new HashMap<>();
 static final Map<String,String> sourceOriginalMethodNames=new HashMap<>();
 static final List<String> edits=new ArrayList<>();
 static String binary(String canonical){return canonicalToBinary.get(canonical.replace('$','.'));}
 static boolean shadowed(TreePath path,String name){
  if(SourceCaptureBindings.resolve(path,name)!=null)return true;
  String namespace="";for(TreePath current=path;current!=null;current=current.getParentPath())if(current.getLeaf() instanceof CompilationUnitTree unit)namespace=unit.getPackageName().toString().replace('.','/')+"/";
  for(TreePath current=path;current!=null;current=current.getParentPath())if(current.getLeaf() instanceof ClassTree clazz){for(Tree member:clazz.getMembers())if(member instanceof VariableTree field&&field.getName().contentEquals(name))return true;if(hierarchyFields.getOrDefault(namespace+clazz.getSimpleName(),Set.of()).contains(name))return true;}
  return false;
 }
 static void addType(Set<String> result,Type type){if(type.getSort()==Type.ARRAY)addType(result,type.getElementType());else if(type.getSort()==Type.OBJECT)result.add(type.getInternalName());else if(type.getSort()==Type.METHOD){addType(result,type.getReturnType());for(Type parameter:type.getArgumentTypes())addType(result,parameter);}}
 static final class References {
  Set<String> types=new HashSet<>();Map<String,Set<String>> fields=new HashMap<>(),methods=new HashMap<>();
  Map<String,References> byMethod=new HashMap<>();
 }
 static References references(ZipFile jar,String owner)throws Exception {
  References result=new References();ZipEntry entry=jar.getEntry(owner+".class");if(entry==null)throw new IllegalStateException("Original normalized owner absent: "+owner);
  new ClassReader(jar.getInputStream(entry)).accept(new ClassVisitor(Opcodes.ASM9){
   @Override public void visit(int version,int access,String name,String signature,String parent,String[] interfaces){result.types.add(name);if(parent!=null)result.types.add(parent);Collections.addAll(result.types,interfaces);}
   @Override public FieldVisitor visitField(int access,String name,String descriptor,String signature,Object value){addType(result.types,Type.getType(descriptor));return null;}
   @Override public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions){
    References methodRefs=new References();result.byMethod.put(name+descriptor,methodRefs);List<References> sinks=List.of(result,methodRefs);
    for(References sink:sinks)addType(sink.types,Type.getMethodType(descriptor));return new MethodVisitor(Opcodes.ASM9){
     void handle(Handle reference){for(References sink:sinks){sink.types.add(reference.getOwner());addType(sink.types,reference.getTag()<=Opcodes.H_PUTSTATIC?Type.getType(reference.getDesc()):Type.getMethodType(reference.getDesc()));}}
     @Override public void visitTypeInsn(int opcode,String type){for(References sink:sinks)if(type.startsWith("["))addType(sink.types,Type.getType(type));else sink.types.add(type);}
     @Override public void visitFieldInsn(int opcode,String type,String field,String desc){for(References sink:sinks){sink.types.add(type);addType(sink.types,Type.getType(desc));if(opcode==Opcodes.GETSTATIC||opcode==Opcodes.PUTSTATIC)sink.fields.computeIfAbsent(type+"\t"+field,ignored->new HashSet<>()).add(opcode+" "+desc);}}
     @Override public void visitMethodInsn(int opcode,String type,String method,String desc,boolean isInterface){for(References sink:sinks){sink.types.add(type);addType(sink.types,Type.getMethodType(desc));sink.methods.computeIfAbsent(type+"\t"+method,ignored->new HashSet<>()).add(opcode+" "+desc);}}
     @Override public void visitInvokeDynamicInsn(String name,String desc,Handle bootstrap,Object... values){for(References sink:sinks)addType(sink.types,Type.getMethodType(desc));for(Object value:values)if(value instanceof Handle target)handle(target);else if(value instanceof Type type)for(References sink:sinks)addType(sink.types,type);}
     @Override public void visitLdcInsn(Object value){if(value instanceof Type type)for(References sink:sinks)addType(sink.types,type);}
    };
   }
  },ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);return result;
 }
 static boolean typePosition(TreePath path){
  Tree child=path.getLeaf();for(TreePath parent=path.getParentPath();parent!=null;child=parent.getLeaf(),parent=parent.getParentPath()){
   Tree tree=parent.getLeaf();if(tree instanceof ParameterizedTypeTree||tree instanceof ArrayTypeTree||tree instanceof AnnotatedTypeTree||tree instanceof UnionTypeTree||tree instanceof IntersectionTypeTree)continue;
   if(tree instanceof VariableTree variable)return variable.getType()==child;
   if(tree instanceof MethodTree method)return method.getReturnType()==child||method.getThrows().contains(child);
   if(tree instanceof TypeCastTree cast)return cast.getType()==child;
   if(tree instanceof InstanceOfTree test)return test.getType()==child;
   if(tree instanceof NewClassTree created)return created.getIdentifier()==child;
   if(tree instanceof NewArrayTree created)return created.getType()==child;
   if(tree instanceof AnnotationTree annotation)return annotation.getAnnotationType()==child;
   if(tree instanceof ClassTree clazz)return clazz.getExtendsClause()==child||clazz.getImplementsClause().contains(child);
   if(tree instanceof MemberSelectTree selected&&selected.getIdentifier().contentEquals("class"))return selected.getExpression()==child;
   return false;
  }return false;
 }
 public static void main(String[] args)throws Exception {
  Path sourceRoot=Path.of(args[0]);Map<String,String> aliases=new HashMap<>();
  for(String line:Files.readAllLines(Path.of(args[2]))){String[] row=line.split("\t");if(row[0].equals("C"))aliases.put(row[1],row[2]);}
  for(String line:Files.readAllLines(Path.of(args[2]))){String[] row=line.split("\t");if(row[0].equals("M")){String descriptor=row[3];for(var alias:aliases.entrySet())descriptor=descriptor.replace("L"+alias.getKey()+";","L"+alias.getValue()+";");sourceOriginalMethodNames.put(aliases.getOrDefault(row[1],row[1])+"\t"+row[4]+descriptor,row[2]);}}
  Map<String,String> parents=new HashMap<>();Map<String,Set<String>> ownFields=new HashMap<>();for(String line:Files.readAllLines(Path.of(args[1]))){String[] row=line.split("\t",-1);String owner=aliases.getOrDefault(row[1],row[1]);if(row[0].equals("C"))parents.put(owner,aliases.getOrDefault(row[4],row[4]));if(row[0].equals("F"))ownFields.computeIfAbsent(owner,ignored->new HashSet<>()).add(row[2]);}
  for(String owner:parents.keySet()){Set<String> fields=new HashSet<>(),visited=new HashSet<>();for(String type=owner;type!=null&&visited.add(type);type=parents.get(type))fields.addAll(ownFields.getOrDefault(type,Set.of()));hierarchyFields.put(owner,fields);}
  for(String line:Files.readAllLines(Path.of(args[1]))){String[] row=line.split("\t");if(!row[0].equals("C"))continue;String owner=aliases.getOrDefault(row[1],row[1]);if(!Files.isRegularFile(sourceRoot.resolve(owner+".java")))continue;String name=owner.replace('/','.');canonicalCandidates.computeIfAbsent(name.replace('$','.'),ignored->new HashSet<>()).add(name);}
  for(var entry:canonicalCandidates.entrySet())if(entry.getValue().size()==1){String name=entry.getValue().iterator().next();if(name.contains("$"))canonicalToBinary.put(entry.getKey(),name);}
  List<java.io.File> files=new ArrayList<>();for(String file:Files.readAllLines(Path.of(args[4])))files.add(new java.io.File(file));JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
  try(ZipFile jar=new ZipFile(args[3]);StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)){
   DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
   for(CompilationUnitTree unit:task.parse()){
    Path file=Path.of(unit.getSourceFile().toUri());String owner=unit.getPackageName().toString().replace('.','/')+"/"+file.getFileName().toString().replaceFirst("\\.java$","");References refs=references(jar,owner);SourcePositions positions=Trees.instance(task).getSourcePositions();Map<String,String> imports=new HashMap<>(),shortAliases=new HashMap<>();Map<String,Set<String>> importedLeaves=new HashMap<>();
    for(ImportTree imported:unit.getImports())if(!imported.isStatic()){
     String original=imported.getQualifiedIdentifier().toString();String simple=original.substring(original.lastIndexOf('.')+1);imports.put(simple,original);String mapped=binary(original);
     if(mapped!=null&&!mapped.equals(original)){shortAliases.put(simple,mapped);edits.add(String.join("\t",file.toString(),""+positions.getStartPosition(unit,imported.getQualifiedIdentifier()),""+positions.getEndPosition(unit,imported.getQualifiedIdentifier()),mapped,"EXACT_ORIGINAL_DECLARATION_IMPORT",mapped.replace('.','/'),"TYPE_IMPORT"));}
     if(mapped!=null&&mapped.contains("$")){String leaf=mapped.substring(mapped.lastIndexOf('$')+1);importedLeaves.computeIfAbsent(leaf,ignored->new HashSet<>()).add(mapped);}
    }
    for(var imported:importedLeaves.entrySet())if(imported.getValue().size()==1){String leaf=imported.getKey(),mapped=imported.getValue().iterator().next();if(!imports.containsKey(leaf)||imports.get(leaf).replace('$','.').equals(mapped.replace('$','.'))){imports.put(leaf,mapped);shortAliases.put(leaf,mapped);}}
    new TreePathScanner<Void,Void>(){
     final Map<Tree,String> memberEvidence=new IdentityHashMap<>();
     final Map<MethodTree,References> methodContexts=new IdentityHashMap<>();
     final Map<References,String> contextKeys=new IdentityHashMap<>();
     String canonical(String text){
      text=text.replaceAll("@[\\w.$]+(?:\\([^)]*\\))?\\s*","").replaceAll("<.*>","").replace(" ","");String suffix="";while(text.endsWith("[]")){suffix+="[]";text=text.substring(0,text.length()-2);}
      String first=text.contains(".")?text.substring(0,text.indexOf('.')):text;if(imports.containsKey(first))return (imports.get(first)+text.substring(first.length())+suffix).replace('$','.');
      if(text.contains(".")&&Character.isLowerCase(text.charAt(0)))return (text+suffix).replace('$','.');
      if(Set.of("void","boolean","byte","char","short","int","long","float","double").contains(text))return text+suffix;
      if(Set.of("Object","String","Integer","Boolean","Long","Number","Throwable","Byte","Character","Short","Float","Double").contains(first))return "java.lang."+text+suffix;
      return (unit.getPackageName()+"."+text+suffix).replace('$','.');
     }
     References methodContext(TreePath path){
      MethodTree method=null;for(TreePath current=path;current!=null;current=current.getParentPath()){if(current.getLeaf() instanceof MethodTree found){method=found;break;}if(current.getLeaf() instanceof ClassTree)return null;}if(method==null)return null;
      if(methodContexts.containsKey(method))return methodContexts.get(method);List<References> matching=new ArrayList<>();String matchingKey=null;
      for(var candidate:refs.byMethod.entrySet()){
       String key=candidate.getKey(),name=key.substring(0,key.indexOf('(')),descriptor=key.substring(key.indexOf('('));String sourceName=method.getName().toString();String originalName=sourceOriginalMethodNames.getOrDefault(owner+"\t"+sourceName+descriptor,sourceName);
       if(!name.equals(sourceName)&&!name.equals(originalName))continue;Type[] parameters=Type.getArgumentTypes(descriptor);if(parameters.length!=method.getParameters().size())continue;
       if(method.getReturnType()!=null&&!canonical(method.getReturnType().toString()).equals(Type.getReturnType(descriptor).getClassName().replace('$','.')))continue;
       boolean same=true;for(int index=0;index<parameters.length;index++)if(!canonical(method.getParameters().get(index).getType().toString()).equals(parameters[index].getClassName().replace('$','.')))same=false;
       if(same){matching.add(candidate.getValue());matchingKey=key;}
      }
      References context=matching.size()==1?matching.get(0):null;methodContexts.put(method,context);if(context!=null)contextKeys.put(context,matchingKey);return context;
     }
     String expand(String text){String first=text.contains(".")?text.substring(0,text.indexOf('.')):text;if(imports.containsKey(first))return imports.get(first)+text.substring(first.length());return text.contains(".")&&Character.isLowerCase(text.charAt(0))?text:unit.getPackageName()+"."+text;}
     boolean originalStaticField(String text){
      int dot=text.lastIndexOf('.');if(dot<0)return false;String owner=expand(text.substring(0,dot));Set<String> candidates=canonicalCandidates.get(owner.replace('$','.'));
      return candidates!=null&&candidates.size()==1&&refs.fields.containsKey(candidates.iterator().next().replace('.','/')+"\t"+text.substring(dot+1));
     }
     boolean originalReference(String mapped,TreePath path){
      String type=mapped.replace('.','/');if(!refs.types.contains(type))return false;
      Tree parent=path.getParentPath().getLeaf();if(parent instanceof MemberSelectTree selected&&selected.getExpression()==path.getLeaf()&&!selected.getIdentifier().contentEquals("class")){
       References scoped=methodContext(path);if(scoped==null)return false;
       Tree grandparent=path.getParentPath().getParentPath()==null?null:path.getParentPath().getParentPath().getLeaf();String name=selected.getIdentifier().toString();
       if(grandparent instanceof MethodInvocationTree invocation&&invocation.getMethodSelect()==selected){
        List<String> matching=new ArrayList<>();for(String method:scoped.methods.getOrDefault(type+"\t"+name,Set.of()))if(Type.getArgumentTypes(method.substring(method.indexOf(' ')+1)).length==invocation.getArguments().size())matching.add(method);
        if(matching.size()!=1||!matching.get(0).startsWith(Opcodes.INVOKESTATIC+" "))return false;memberEvidence.put(path.getLeaf(),"ENCLOSING="+contextKeys.get(scoped)+" "+type+" "+name+" "+matching.get(0));return true;
       }
       int opcode=grandparent instanceof AssignmentTree assigned&&assigned.getVariable()==selected?Opcodes.PUTSTATIC:Opcodes.GETSTATIC;
       List<String> matching=scoped.fields.getOrDefault(type+"\t"+name,Set.of()).stream().filter(field->field.startsWith(opcode+" ")).toList();if(matching.size()!=1)return false;memberEvidence.put(path.getLeaf(),"ENCLOSING="+contextKeys.get(scoped)+" "+type+" "+name+" "+matching.get(0));return true;
      }return !(parent instanceof MemberReferenceTree);
     }
     void edit(Tree tree,String mapped,String proof){edits.add(String.join("\t",file.toString(),""+positions.getStartPosition(unit,tree),""+positions.getEndPosition(unit,tree),mapped,proof,mapped.replace('.','/'),memberEvidence.getOrDefault(tree,"TYPE_REFERENCE")));}
     @Override public Void visitImport(ImportTree imported,Void unused){return null;}
     @Override public Void visitMemberSelect(MemberSelectTree selected,Void unused){
      String text=selected.toString();if(text.matches("[A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*")){
       String mapped=binary(expand(text));Tree first=selected;while(first instanceof MemberSelectTree member)first=member.getExpression();boolean local=first instanceof IdentifierTree identifier&&shadowed(TreePath.getPath(unit,identifier),identifier.getName().toString());
       if(mapped!=null&&!mapped.equals(text)&&(!local||typePosition(getCurrentPath()))&&(typePosition(getCurrentPath())||!originalStaticField(text))&&originalReference(mapped,getCurrentPath())){edit(selected,mapped,"EXACT_ORIGINAL_REFERENCED_BINARY_OWNER");return null;}
      }return super.visitMemberSelect(selected,unused);
     }
     @Override public Void visitIdentifier(IdentifierTree identifier,Void unused){
      String mapped=shortAliases.get(identifier.getName().toString());if(mapped==null)return null;Tree parent=getCurrentPath().getParentPath().getLeaf();boolean qualifier=parent instanceof MemberSelectTree selected&&selected.getExpression()==identifier||parent instanceof MemberReferenceTree reference&&reference.getQualifierExpression()==identifier;
      if((typePosition(getCurrentPath())||qualifier&&!shadowed(getCurrentPath(),identifier.getName().toString()))&&originalReference(mapped,getCurrentPath()))edit(identifier,mapped,"EXACT_IMPORTED_OWNER_AND_ORIGINAL_REFERENCE");return null;
     }
    }.scan(unit,null);
   }
   long errors=diagnostics.getDiagnostics().stream().filter(diagnostic->diagnostic.getKind()==Diagnostic.Kind.ERROR).count();if(errors!=0)throw new IllegalStateException("Source parse errors: "+errors);
  }
  Collections.sort(edits);Files.write(Path.of(args[5]),edits);System.out.println("Exact owned binary type reference edits "+edits.size());
 }
}
