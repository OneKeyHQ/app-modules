import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

/** Export source-recovery lambda captures; no instruction payload enters the product build. */
public final class ExportLambdaSites {
    private static final class OffsetReader extends ClassReader {
        int offset;
        OffsetReader(byte[] bytes) { super(bytes); }
        @Override protected void readBytecodeInstructionOffset(int value) { offset = value; }
    }
    private static Map<String,List<Integer>> bytecodeOffsets(byte[] bytes) {
        OffsetReader reader = new OffsetReader(bytes);
        Map<String,List<Integer>> offsets = new HashMap<>();
        reader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override public MethodVisitor visitMethod(int access,String name,String descriptor,String signature,String[] exceptions) {
                List<Integer> methodOffsets = new ArrayList<>();
                offsets.put(name+descriptor,methodOffsets);
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override public void visitInvokeDynamicInsn(String name,String descriptor,Handle bootstrap,Object... arguments) {
                        if (bootstrap.getOwner().equals("java/lang/invoke/LambdaMetafactory")) methodOffsets.add(reader.offset);
                    }
                };
            }
        },0);
        return offsets;
    }
    public static void main(String[] args) throws Exception {
        List<String> rows = new ArrayList<>();
        List<String> bootstrapRows = new ArrayList<>();
        List<String> assignmentRows = new ArrayList<>();
        List<String> fieldCaptureRows = new ArrayList<>();
        try (ZipFile jar = new ZipFile(args[0])) {
            for (ZipEntry entry : Collections.list(jar.entries())) {
                if (!entry.getName().endsWith(".class")) continue;
                ClassNode node = new ClassNode();
                byte[] bytes = jar.getInputStream(entry).readAllBytes();
                new ClassReader(bytes).accept(node, 0);
                Map<String,List<Integer>> offsets = bytecodeOffsets(bytes);
                for (MethodNode method : node.methods) {
                    if (method.instructions.size() == 0) continue;
                    int lambdaOrdinal = 0;
                    Frame<SourceValue>[] frames = new Analyzer<>(new SourceInterpreter()).analyze(node.name, method);
                    for (int i = 0; i < method.instructions.size(); i++) {
                        if (!(method.instructions.get(i) instanceof InvokeDynamicInsnNode site)) continue;
                        if (!site.bsm.getOwner().equals("java/lang/invoke/LambdaMetafactory")) continue;
                        Handle implementation = (Handle) site.bsmArgs[1];
                        Type[] captured = Type.getArgumentTypes(site.desc);
                        List<String> captures = new ArrayList<>();
                        Frame<SourceValue> frame = frames[i];
                        for (int j = 0; j < captured.length; j++) {
                            SourceValue value = frame.getStack(frame.getStackSize() - captured.length + j);
                            List<String> producers = new ArrayList<>();
                            for (AbstractInsnNode producer : value.insns) {
                                producers.add(producer instanceof VarInsnNode local ? "var" + local.var : "opcode" + producer.getOpcode());
                            }
                            Collections.sort(producers);
                            captures.add(captured[j].getDescriptor() + "=" + String.join("+", producers));
                            if(value.insns.size()==1&&value.insns.iterator().next() instanceof FieldInsnNode field&&field.getOpcode()==Opcodes.GETFIELD){
                                Frame<SourceValue> beforeField=frames[method.instructions.indexOf(field)];SourceValue receiver=beforeField.getStack(beforeField.getStackSize()-1);List<String> receiverProducers=new ArrayList<>();
                                for(AbstractInsnNode producer:receiver.insns)receiverProducers.add(producer instanceof VarInsnNode local?"var"+local.var:"opcode"+producer.getOpcode());Collections.sort(receiverProducers);
                                fieldCaptureRows.add(String.join("\t",node.name,method.name,method.desc,""+offsets.get(method.name+method.desc).get(lambdaOrdinal),""+j,field.owner,field.name,field.desc,String.join("+",receiverProducers)));
                            }
                        }
                        String bci = ""+offsets.get(method.name+method.desc).get(lambdaOrdinal++);
                        bootstrapRows.add(String.join("\t",node.name,method.name,method.desc,bci,site.bsm.getOwner(),site.bsm.getName(),site.bsm.getDesc(),""+site.bsmArgs.length,Arrays.toString(site.bsmArgs)));
                        for(int next=i+1;next<method.instructions.size();next++) {
                            AbstractInsnNode following=method.instructions.get(next);
                            if(following instanceof JumpInsnNode||following instanceof TableSwitchInsnNode||following instanceof LookupSwitchInsnNode||following instanceof InvokeDynamicInsnNode||following.getOpcode()>=Opcodes.IRETURN&&following.getOpcode()<=Opcodes.RETURN)break;
                            if(following instanceof FieldInsnNode field&&field.getOpcode()==Opcodes.PUTFIELD){assignmentRows.add(String.join("\t",node.name,method.name,method.desc,bci,field.owner,field.name,field.desc));break;}
                        }
                        String row = String.join("\t", node.name, method.name, method.desc, ""+i,
                            implementation.getOwner(), implementation.getName(), implementation.getDesc(),
                            ""+implementation.getTag(), ((Type)site.bsmArgs[0]).getDescriptor(),
                            ((Type)site.bsmArgs[2]).getDescriptor(), Type.getReturnType(site.desc).getInternalName(),
                            String.join(",", captures));
                        rows.add(args.length > 2 && args[2].equals("--with-bci") ? row+"\t"+bci : row);
                    }
                }
            }
        }
        Collections.sort(rows);
        Files.write(Path.of(args[1]), rows);
        Collections.sort(bootstrapRows);
        Files.write(Path.of(args[1]+".bootstrap.tsv"), bootstrapRows);
        Collections.sort(assignmentRows);
        Files.write(Path.of(args[1]+".assignment.tsv"), assignmentRows);
        Collections.sort(fieldCaptureRows);
        Files.write(Path.of(args[1]+".field-captures.tsv"), fieldCaptureRows);
        System.out.println("Exported " + rows.size() + " LambdaMetafactory sites");
    }
}
