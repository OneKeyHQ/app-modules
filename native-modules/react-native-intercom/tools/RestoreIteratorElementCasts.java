import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Recover original reference Iterator.next/CHECKCAST/ASTORE headers from reviewed methods. */
public final class RestoreIteratorElementCasts {
 static final class OffsetReader extends ClassReader {int offset;OffsetReader(byte[] bytes){super(bytes);}@Override protected void readBytecodeInstructionOffset(int value){offset=value;}}
 static final Map<String,Map<String,List<String>>> loops=new HashMap<>();
 static final Map<String,Set<Long>> errors=new HashMap<>();
 static final List<String> edits=new ArrayList<>(),rejections=new ArrayList<>();
 static final Map<String,Map<String,Set<String>>> reviewed=new HashMap<>();
 static boolean reviewedMode;
 static Set<String> targets(String owner,String method){return reviewedMode?reviewed.getOrDefault(owner,Map.of()).getOrDefault(method,Set.of()):Set.of("java/util/Map$Entry");}
 static AbstractInsnNode previous(AbstractInsnNode node){do{node=node.getPrevious();}while(node!=null&&node.getOpcode()<0);return node;}
 static AbstractInsnNode next(AbstractInsnNode node){do{node=node.getNext();}while(node!=null&&node.getOpcode()<0);return node;}
 static void originals(Path jar)throws Exception {
  try(ZipFile archive=new ZipFile(jar.toFile())){for(ZipEntry entry:Collections.list(archive.entries()))if(entry.getName().endsWith(".class")){
   byte[] bytes=archive.getInputStream(entry).readAllBytes();OffsetReader reader=new OffsetReader(bytes);Map<String,List<Integer>> offsets=new HashMap<>();
   reader.accept(new ClassVisitor(Opcodes.ASM9){@Override public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions){return new MethodVisitor(Opcodes.ASM9){@Override public void visitTypeInsn(int opcode,String type){if(opcode==Opcodes.CHECKCAST&&targets(entry.getName().replaceFirst("\\.class$",""),name+descriptor).contains(type))offsets.computeIfAbsent(name+descriptor+"\t"+type,ignored->new ArrayList<>()).add(reader.offset);}};}},0);
   ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);Map<String,List<String>> methods=new HashMap<>();
   for(MethodNode method:node.methods)for(String target:targets(node.name,method.name+method.desc)){int ordinal=0;List<String> sites=new ArrayList<>();for(AbstractInsnNode instruction:method.instructions)if(instruction instanceof TypeInsnNode cast&&cast.getOpcode()==Opcodes.CHECKCAST&&cast.desc.equals(target)){
    int bci=offsets.get(method.name+method.desc+"\t"+target).get(ordinal++);AbstractInsnNode before=previous(cast),after=next(cast);
    if(before instanceof MethodInsnNode call&&call.getOpcode()==Opcodes.INVOKEINTERFACE&&call.owner.equals("java/util/Iterator")&&call.name.equals("next")&&call.desc.equals("()Ljava/lang/Object;")&&after instanceof VarInsnNode store&&store.getOpcode()==Opcodes.ASTORE)sites.add("REFERENCE_BCI="+bci+":STORE_SLOT="+store.var+":INVOKEINTERFACE=java/util/Iterator.next()Ljava/lang/Object;:CHECKCAST="+target);
   }if(!sites.isEmpty())methods.put(method.name+method.desc+"\t"+target,sites);}
   if(!methods.isEmpty())loops.put(node.name,methods);
  }}
 }
 public static void main(String[] args)throws Exception {
  reviewedMode=args.length>4;if(reviewedMode)for(String line:Files.readAllLines(Path.of(args[4]))){String[] row=line.split("\t");if(row[3].startsWith("["))throw new IllegalArgumentException("Array elements require separate proof");reviewed.computeIfAbsent(row[0],ignored->new HashMap<>()).computeIfAbsent(row[1]+row[2],ignored->new HashSet<>()).add(row[3]);}
  originals(Path.of(args[0]));Pattern error=Pattern.compile("^(.*\\.java):(\\d+): error: incompatible types: Object cannot be converted to "+(reviewedMode?".+":"Entry")+"$");for(String line:Files.readAllLines(Path.of(args[2]))){Matcher match=error.matcher(line);if(match.matches())errors.computeIfAbsent(match.group(1),ignored->new HashSet<>()).add(Long.parseLong(match.group(2)));}
  List<java.io.File> files=new ArrayList<>();for(String file:errors.keySet())files.add(new java.io.File(file));JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
  try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)){
   DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
   for(CompilationUnitTree unit:task.parse()){
    String file=Path.of(unit.getSourceFile().toUri()).toString(),source=Files.readString(Path.of(file));SourcePositions positions=Trees.instance(task).getSourcePositions();Map<String,String> imports=new HashMap<>();for(ImportTree imported:unit.getImports()){String name=imported.getQualifiedIdentifier().toString();imports.put(name.substring(name.lastIndexOf('.')+1),name);}
    new TreePathScanner<Void,Void>(){
     @Override public Void visitMethod(MethodTree method,Void unused){
      if(method.getBody()==null||!(getCurrentPath().getParentPath().getLeaf() instanceof ClassTree clazz))return null;
      RememberFunctionalCasts.Context context=new RememberFunctionalCasts.Context();context.unit=unit;context.method=method;context.imports=imports;context.owner=unit.getPackageName().toString().replace('.','/')+"/"+clazz.getSimpleName();
      Set<String> candidates=new LinkedHashSet<>();for(String fullkey:loops.getOrDefault(context.owner,Map.of()).keySet()){String key=fullkey.split("\t")[0];
       String name=key.substring(0,key.indexOf('(')),descriptor=key.substring(key.indexOf('('));Type[] parameters=Type.getArgumentTypes(descriptor);if(!name.equals(method.getName().toString())||parameters.length!=method.getParameters().size()||!(method.getName().contentEquals("<init>")?Type.getReturnType(descriptor).equals(Type.VOID_TYPE):context.type(method.getReturnType(),Type.getReturnType(descriptor))))continue;
       boolean same=true;for(int index=0;index<parameters.length;index++)if(!context.type(method.getParameters().get(index).getType(),parameters[index]))same=false;if(same)candidates.add(key);
      }if(candidates.size()!=1)return null;String key=candidates.iterator().next();for(String target:targets(context.owner,key)){List<EnhancedForLoopTree> sourceLoops=new ArrayList<>();
      new TreeScanner<Void,Void>(){@Override public Void visitClass(ClassTree nested,Void ignored){return null;}@Override public Void visitLambdaExpression(LambdaExpressionTree nested,Void ignored){return null;}@Override public Void visitEnhancedForLoop(EnhancedForLoopTree loop,Void ignored){if(context.type(loop.getVariable().getType(),Type.getObjectType(target)))sourceLoops.add(loop);return super.visitEnhancedForLoop(loop,ignored);}}.scan(method.getBody(),null);
      sourceLoops.sort(Comparator.comparingLong(loop->positions.getStartPosition(unit,loop)));List<String> original=loops.get(context.owner).get(key+"\t"+target);if(original==null)continue;
      if(original.size()!=sourceLoops.size()){rejections.add(file+"\t"+key+"\t"+"entry loop count/order ambiguous: source="+sourceLoops.size()+",original="+original.size());return null;}
      for(int ordinal=0;ordinal<sourceLoops.size();ordinal++){
       EnhancedForLoopTree loop=sourceLoops.get(ordinal);long start=positions.getStartPosition(unit,loop),line=unit.getLineMap().getLineNumber(start);if(!errors.getOrDefault(file,Set.of()).contains(line))continue;
       if(!(loop.getStatement() instanceof BlockTree block)){rejections.add(file+"\t"+key+"\t"+"nonblock loop body");continue;}
       if(!reviewedMode&&(!(loop.getExpression() instanceof MethodInvocationTree invocation)||!invocation.getArguments().isEmpty()||!(invocation.getMethodSelect() instanceof MemberSelectTree selected)||!selected.getIdentifier().contentEquals("entrySet"))){rejections.add(file+"\t"+key+"\t"+"source iterable is not direct entrySet()");continue;}
       long variableStart=positions.getStartPosition(unit,loop.getVariable()),variableEnd=positions.getEndPosition(unit,loop.getVariable()),body=positions.getStartPosition(unit,block)+1;String variable=source.substring((int)variableStart,(int)variableEnd),identifier="recoveredEntryElement_"+ordinal+"_"+line;if(source.contains(identifier))throw new IllegalStateException("Recovery local already exists");
       String indent=source.substring(source.lastIndexOf('\n',(int)start)+1,(int)start);String declaration="\n"+indent+"   "+variable+" = ("+loop.getVariable().getType()+") "+identifier+";";
       edits.add(String.join("\t",file,""+variableStart,""+variableEnd,"java.lang.Object "+identifier,""+body,declaration.replace("\n","\\n"),context.owner,key,""+ordinal,original.get(ordinal),""+line,target));
      }}return null;
     }
    }.scan(unit,null);
   }
   if(diagnostics.getDiagnostics().stream().anyMatch(diagnostic->diagnostic.getKind()==Diagnostic.Kind.ERROR))throw new IllegalStateException("Source parse failed");
  }Files.write(Path.of(args[3]),edits);Files.write(Path.of(args[3]+".rejected.tsv"),rejections);System.out.println("Proved Iterator entry casts="+edits.size()+", rejected methods="+rejections.size());
 }
}
