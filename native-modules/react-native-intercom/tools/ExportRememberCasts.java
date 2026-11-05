import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

/** Export exact original Composer-group CHECKCAST/store/end joins as source recovery evidence. */
public final class ExportRememberCasts {
 static final class OffsetReader extends ClassReader {
  int offset;OffsetReader(byte[] bytes){super(bytes);}
  @Override protected void readBytecodeInstructionOffset(int value){offset=value;}
 }
 static AbstractInsnNode next(AbstractInsnNode instruction){do{instruction=instruction.getNext();}while(instruction!=null&&(instruction.getOpcode()<0||instruction.getOpcode()==Opcodes.NOP));return instruction;}
 static Integer integer(AbstractInsnNode instruction){
  if(instruction instanceof LdcInsnNode ldc&&ldc.cst instanceof Integer value)return value;
  if(instruction instanceof IntInsnNode immediate)return immediate.operand;
  int opcode=instruction.getOpcode();return opcode>=Opcodes.ICONST_M1&&opcode<=Opcodes.ICONST_5?opcode-Opcodes.ICONST_0:null;
 }
 public static void main(String[] args)throws Exception {
  List<String> rows=new ArrayList<>(),hierarchy=new ArrayList<>();int unmatched=0;
  for(int input=1;input<args.length;input++)try(ZipFile archive=new ZipFile(args[input])){
   for(ZipEntry entry:Collections.list(archive.entries()))if(entry.getName().endsWith(".class")){
    byte[] bytes=archive.getInputStream(entry).readAllBytes();ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);hierarchy.add(String.join("\t",node.name,node.superName==null?"":node.superName,String.join(",",node.interfaces)));
    OffsetReader reader=new OffsetReader(bytes);Map<String,List<Integer>> castOffsets=new HashMap<>();
    reader.accept(new ClassVisitor(Opcodes.ASM9){
     @Override public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions){
      List<Integer> offsets=new ArrayList<>();castOffsets.put(name+descriptor,offsets);
      return new MethodVisitor(Opcodes.ASM9){@Override public void visitTypeInsn(int opcode,String type){if(opcode==Opcodes.CHECKCAST)offsets.add(reader.offset);}};
     }
    },0);
    for(MethodNode method:node.methods){
     if(method.instructions.size()==0)continue;
     Frame<SourceValue>[] frames=new Analyzer<>(new SourceInterpreter()).analyze(node.name,method);
     Deque<Long> groups=new ArrayDeque<>();int castOrdinal=0;
     for(int i=0;i<method.instructions.size();i++){
      AbstractInsnNode instruction=method.instructions.get(i);
      if(instruction instanceof MethodInsnNode call&&call.owner.equals("androidx/compose/runtime/Composer")){
       if(call.name.equals("startReplaceGroup")&&call.desc.equals("(I)V")){
        Frame<SourceValue> frame=frames[i];Integer key=null;
        if(frame!=null){SourceValue value=frame.getStack(frame.getStackSize()-1);if(value.insns.size()==1)key=integer(value.insns.iterator().next());}
        groups.push(key==null?Long.MIN_VALUE:key.longValue());
       }else if(call.name.equals("endReplaceGroup")&&call.desc.equals("()V")){if(!groups.isEmpty())groups.pop();}
      }
      if(!(instruction instanceof TypeInsnNode cast)||cast.getOpcode()!=Opcodes.CHECKCAST)continue;
      int bci=castOffsets.get(method.name+method.desc).get(castOrdinal++);
      if(!cast.desc.matches("kotlin/jvm/functions/Function[0-9]+"))continue;
      AbstractInsnNode store=next(cast),receiver=store==null?null:next(store),end=receiver==null?null:next(receiver);
      if(groups.isEmpty()||groups.peek()==Long.MIN_VALUE||!(store instanceof VarInsnNode local)||local.getOpcode()!=Opcodes.ASTORE||!(receiver instanceof VarInsnNode load)||load.getOpcode()!=Opcodes.ALOAD||!(end instanceof MethodInsnNode close)||!close.owner.equals("androidx/compose/runtime/Composer")||!close.name.equals("endReplaceGroup")||!close.desc.equals("()V")){unmatched++;continue;}
      rows.add(String.join("\t",node.name,method.name,method.desc,""+groups.peek(),cast.desc,""+bci,""+local.var,""+load.var));
     }
    }
   }
  }
  Collections.sort(rows);Collections.sort(hierarchy);Files.write(Path.of(args[0]),rows);Files.write(Path.of(args[0]+".hierarchy.tsv"),hierarchy);System.out.println("Exact remember cast/store/end joins "+rows.size()+", nonmatching casts "+unmatched);
 }
}
