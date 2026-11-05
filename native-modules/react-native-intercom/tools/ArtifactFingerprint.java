import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import org.objectweb.asm.tree.*;

// Structural hashes identify compiler-generated accessors; no original instruction bytes are used in source builds.
public final class ArtifactFingerprint {
    public static String of(MethodNode method) {
        Map<LabelNode,Integer> labels=new IdentityHashMap<>();int index=0;
        for(AbstractInsnNode n:method.instructions){if(n instanceof LabelNode l)labels.put(l,index);else if(n.getOpcode()>=0)index++;}
        StringBuilder text=new StringBuilder();
        for(AbstractInsnNode n:method.instructions) {
            if(n.getOpcode()<0)continue;text.append(n.getOpcode()).append(':');
            if(n instanceof VarInsnNode v)text.append(v.var);
            else if(n instanceof IntInsnNode v)text.append(v.operand);
            else if(n instanceof TypeInsnNode v)text.append(v.desc);
            else if(n instanceof FieldInsnNode v)text.append(v.owner).append('/').append(v.name).append(v.desc);
            else if(n instanceof MethodInsnNode v)text.append(v.owner).append('/').append(v.name).append(v.desc).append('/').append(v.itf);
            else if(n instanceof LdcInsnNode v)text.append(v.cst.getClass().getName()).append('/').append(v.cst);
            else if(n instanceof IincInsnNode v)text.append(v.var).append('/').append(v.incr);
            else if(n instanceof JumpInsnNode v)text.append(labels.get(v.label));
            else if(n instanceof TableSwitchInsnNode v){text.append(v.min).append('/').append(v.max).append('/').append(labels.get(v.dflt));for(LabelNode l:v.labels)text.append('/').append(labels.get(l));}
            else if(n instanceof LookupSwitchInsnNode v){text.append(v.keys).append('/').append(labels.get(v.dflt));for(LabelNode l:v.labels)text.append('/').append(labels.get(l));}
            else if(n instanceof MultiANewArrayInsnNode v)text.append(v.desc).append('/').append(v.dims);
            else if(n instanceof InvokeDynamicInsnNode v)text.append(v.name).append(v.desc).append(v.bsm).append(Arrays.toString(v.bsmArgs));
            text.append('\n');
        }
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text.toString().getBytes(StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}
    }
}
