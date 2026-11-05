import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Account for exact source compiler artifacts; never read vendor classes or import method bodies. */
public final class PrepareBaseCompilerArtifacts {
    static final String MEDIA="io/intercom/android/sdk/m5/upload/data/MediaData$Media";
    static List<AbstractInsnNode> code(MethodNode method) {
        return Arrays.stream(method.instructions.toArray()).filter(n->n.getOpcode()>=0).toList();
    }
    static boolean forwarder(MethodNode method,String owner,String returnDescriptor,boolean itf) {
        List<AbstractInsnNode> c=code(method);
        return method.tryCatchBlocks.isEmpty() && c.size()==3
            && c.get(0) instanceof VarInsnNode load && load.getOpcode()==Opcodes.ALOAD && load.var==0
            && c.get(1) instanceof MethodInsnNode call && call.owner.equals(owner) && call.name.equals("getDataSource")
            && call.desc.equals("()"+returnDescriptor) && call.itf==itf
            && call.getOpcode()==(itf?Opcodes.INVOKEINTERFACE:Opcodes.INVOKEVIRTUAL) && c.get(2).getOpcode()==Opcodes.ARETURN;
    }
    static void switchMap(ClassNode node) {
        if((node.access&Opcodes.ACC_SYNTHETIC)==0 || !node.superName.equals("java/lang/Object") || !node.interfaces.isEmpty()
           || node.fields.isEmpty() || node.fields.size()>2 || node.methods.size()!=1)throw new IllegalStateException("Not a compiler switch map: "+node.name);
        for(FieldNode field:node.fields)if(!field.name.startsWith("$SwitchMap$") || !field.desc.equals("[I")
            || (field.access&(Opcodes.ACC_STATIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC))!=(Opcodes.ACC_STATIC|Opcodes.ACC_FINAL|Opcodes.ACC_SYNTHETIC))throw new IllegalStateException("Switch map field shape");
        MethodNode method=node.methods.get(0);
        if(!method.name.equals("<clinit>") || !method.desc.equals("()V"))throw new IllegalStateException("Switch map business method");
        Set<String> enums=new HashSet<>();
        for(AbstractInsnNode instruction:method.instructions) {
            if(instruction instanceof MethodInsnNode call) {
                if(call.getOpcode()==Opcodes.INVOKESTATIC && call.name.equals("values") && call.desc.equals("()[L"+call.owner+";"))enums.add(call.owner);
                else if(!(call.getOpcode()==Opcodes.INVOKEVIRTUAL && call.name.equals("ordinal") && call.desc.equals("()I")))throw new IllegalStateException("Unexpected switch map invocation");
            } else if(instruction instanceof FieldInsnNode field) {
                if(field.getOpcode()==Opcodes.PUTSTATIC) {
                    if(!field.owner.equals(node.name)||!field.desc.equals("[I"))throw new IllegalStateException("Switch map writes another owner");
                } else if(field.getOpcode()!=Opcodes.GETSTATIC || !(field.owner.equals(node.name)&&field.desc.equals("[I")) && !field.desc.equals("L"+field.owner+";"))throw new IllegalStateException("Switch map field operation");
            } else if(instruction instanceof TypeInsnNode || instruction instanceof InvokeDynamicInsnNode || instruction instanceof MultiANewArrayInsnNode)throw new IllegalStateException("Switch map allocation/indy");
        }
        if(enums.isEmpty())throw new IllegalStateException("Switch map has no enum");
        for(AbstractInsnNode instruction:method.instructions)if(instruction instanceof MethodInsnNode call && call.name.equals("ordinal") && !call.owner.equals("java/lang/Enum") && !enums.contains(call.owner))throw new IllegalStateException("Switch map ordinal owner is not an enum values owner");
        for(TryCatchBlockNode handler:method.tryCatchBlocks)if(!Objects.equals(handler.type,"java/lang/NoSuchFieldError"))throw new IllegalStateException("Switch map unexpected handler");
    }
    static String text(String value){return value==null?"":value;}
    static void metadata(ClassNode node,List<String> rows) {
        rows.add(String.join("\t","C",node.name,"52",""+node.access,text(node.superName),text(node.signature),String.join(",",node.interfaces)));
        for(FieldNode f:node.fields)rows.add(String.join("\t","F",node.name,f.name,f.desc,""+f.access,text(f.signature)));
        for(MethodNode m:node.methods)rows.add(String.join("\t","M",node.name,m.name,m.desc,""+m.access,text(m.signature),String.join(",",m.exceptions)));
        // Synthetic switch map classes have no source-visible enclosing declaration contract.
        // Their field/method Code remains the actual compiler output.
    }
    public static void main(String[] args)throws Exception {
        Path input=Path.of(args[0]), output=Path.of(args[4]);
        List<String> original=Files.readAllLines(Path.of(args[1])), augmented=new ArrayList<>(original), report=new ArrayList<>();
        Map<String,Set<String>> methods=new HashMap<>();Map<String,String> fingerprints=new HashMap<>();Set<String> originalClasses=new HashSet<>();
        for(String line:original) {String[] r=line.split("\t",-1);if(r[0].equals("C"))originalClasses.add(r[1]);if(r[0].equals("M"))methods.computeIfAbsent(r[1],unused->new HashSet<>()).add(r[2]+r[3]);if(r[0].equals("P"))fingerprints.put(r[1]+"\t"+r[2]+r[3],r[4]);}
        Map<String,ClassNode> classes=new TreeMap<>();Map<String,Path> paths=new HashMap<>();
        try(var walk=Files.walk(input)){for(Path p:walk.filter(x->x.toString().endsWith(".class")).sorted().toList()){ClassNode n=new ClassNode();new ClassReader(Files.readAllBytes(p)).accept(n,0);classes.put(n.name,n);paths.put(n.name,p);}}
        Set<String> modified=new HashSet<>(), recorded=new HashSet<>();int classCount=0,factoryCount=0,removed=0;
        for(String line:Files.readAllLines(Path.of(args[2]))) {
            String[] r=line.split("\t",-1);if(!recorded.add(line))throw new IllegalStateException("Duplicate compiler artifact policy");ClassNode n=classes.get(r[1]);if(n==null)throw new IllegalStateException("Declared compiler artifact missing: "+line);
            if(r[0].equals("C")) {
                if(originalClasses.contains(n.name))throw new IllegalStateException("Original class relabeled compiler artifact");switchMap(n);metadata(n,augmented);classCount++;report.add("SOURCE_COMPILED_SWITCH_MAP\t"+n.name);
            } else if(r[0].equals("M")) {
                if(methods.getOrDefault(n.name,Set.of()).contains(r[2]+r[3]))throw new IllegalStateException("Original method relabeled compiler artifact");
                MethodNode helper=n.methods.stream().filter(m->m.name.equals(r[2])&&m.desc.equals(r[3])).findFirst().orElseThrow();
                MethodNode originalFactory=n.methods.stream().filter(m->m.name.equals("$values")&&m.desc.equals(r[3])).findFirst().orElseThrow();
                if(!r[2].equals("$values$") || !r[3].equals("()[L"+n.name+";") || helper.access!=(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC|Opcodes.ACC_SYNTHETIC)
                   || !helper.tryCatchBlocks.isEmpty() || !originalFactory.tryCatchBlocks.isEmpty()
                   || !ArtifactFingerprint.of(helper).equals(ArtifactFingerprint.of(originalFactory))
                   || !ArtifactFingerprint.of(originalFactory).equals(fingerprints.get(n.name+"\t$values"+r[3])))throw new IllegalStateException("Unproven enum array factory: "+n.name);
                augmented.add(String.join("\t","M",n.name,helper.name,helper.desc,""+helper.access,text(helper.signature),String.join(",",helper.exceptions)));
                factoryCount++;report.add("SOURCE_COMPILED_ENUM_ARRAY_FACTORY\t"+n.name+"\t"+helper.name+helper.desc);
            } else if(r[0].equals("R")) {
                if(!n.name.equals(MEDIA)||!r[2].equals("getDataSource")||!r[3].equals("()Ljava/lang/Object;") || (n.access&Opcodes.ACC_INTERFACE)==0
                   || methods.get(n.name).contains(r[2]+r[3]))throw new IllegalStateException("Unrecognized compiler method removal");
                MethodNode bridge=n.methods.stream().filter(m->m.name.equals(r[2])&&m.desc.equals(r[3])).findFirst().orElseThrow();
                if(bridge.access!=(Opcodes.ACC_PUBLIC|Opcodes.ACC_BRIDGE|Opcodes.ACC_SYNTHETIC) || !forwarder(bridge,n.name,"Landroid/net/Uri;",true))throw new IllegalStateException("Interface bridge is not a pure covariant forwarder");
                for(String name:List.of("Image","Other","Video")) {
                    String owner=MEDIA+"$"+name;ClassNode child=classes.get(owner);
                    if(child==null||!methods.get(owner).contains("getDataSource()Ljava/lang/Object;")||!methods.get(owner).contains("getDataSource()Landroid/net/Uri;"))throw new IllegalStateException("Original concrete media bridge contract missing");
                    MethodNode concrete=child.methods.stream().filter(m->m.name.equals("getDataSource")&&m.desc.equals("()Ljava/lang/Object;")).findFirst().orElseThrow();
                    if((concrete.access&(Opcodes.ACC_BRIDGE|Opcodes.ACC_SYNTHETIC))!=(Opcodes.ACC_BRIDGE|Opcodes.ACC_SYNTHETIC)||!forwarder(concrete,owner,"Landroid/net/Uri;",false))throw new IllegalStateException("Concrete original media bridge missing");
                }
                n.methods.remove(bridge);modified.add(n.name);removed++;report.add("REMOVE_JAVAC_COVARIANT_INTERFACE_BRIDGE\t"+n.name+"\t"+bridge.name+bridge.desc);
            } else throw new IllegalStateException("Unknown compiler artifact policy");
        }
        if(classCount!=43||factoryCount!=8||removed!=1)throw new IllegalStateException("Compiler artifact inventory drift");
        Files.createDirectories(output);
        for(ClassNode n:classes.values()) {Path target=output.resolve(n.name+".class");Files.createDirectories(target.getParent());if(modified.contains(n.name)){ClassWriter writer=new ClassWriter(0);n.accept(writer);Files.write(target,writer.toByteArray());}else Files.copy(paths.get(n.name),target);}
        Files.write(Path.of(args[3]),augmented);Files.write(Path.of(args[5]),report);
        System.out.println("Original classes="+originalClasses.size()+"; separately declared source compiler classes="+classCount+"; exact original-body enum array factories="+factoryCount+"; removed compiler-only interface bridges="+removed);
    }
}
