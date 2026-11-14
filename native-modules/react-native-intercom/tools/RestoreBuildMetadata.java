import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;

/** Restore ABI metadata and remove verified compiler-only artifacts; retain source-compiled method bodies. */
public final class RestoreBuildMetadata {
    static final class TypeSpec {
        String[] type, enclosing;
        final Map<String,String[]> fields = new TreeMap<>(), methods = new TreeMap<>();
        final List<String[]> inners = new ArrayList<>();
    }
    static String nullable(String text) { return text.isEmpty() ? null : text; }
    private static boolean emptyJavaConstructor(MethodNode method) {
        if (!method.desc.equals("()V") || !method.tryCatchBlocks.isEmpty()) return false;
        List<AbstractInsnNode> code=new ArrayList<>();
        for (AbstractInsnNode instruction : method.instructions) if (instruction.getOpcode()>=0) code.add(instruction);
        return code.size()==3 && code.get(0) instanceof VarInsnNode load && load.getOpcode()==Opcodes.ALOAD && load.var==0
            && code.get(1) instanceof MethodInsnNode call && call.getOpcode()==Opcodes.INVOKESPECIAL
            && call.owner.equals("java/lang/Object") && call.name.equals("<init>") && call.desc.equals("()V") && !call.itf
            && code.get(2).getOpcode()==Opcodes.RETURN;
    }
    public static void main(String[] args) throws Exception {
        Path compiled=Path.of(args[0]), output=Path.of(args[1]);
        Map<String,String> types=new HashMap<>(), members=new HashMap<>(), ownedMembers=new HashMap<>();
        for (String line : Files.readAllLines(Path.of(args[2]))) {
            String[] row=line.split("\t",-1);
            if (row[0].equals("C")) types.put(row[2],row[1]);
            else {
                ownedMembers.put(row[0]+"\t"+row[1]+"\t"+row[4]+"\t"+row[3],row[2]);
                // Only the original global normalizer aliases may cross an inherited owner.
                if (row[4].startsWith("recovered$")) members.put(row[0]+"\t"+row[4]+"\t"+row[3],row[2]);
            }
        }
        Remapper names=new Remapper() { @Override public String map(String name) { return types.getOrDefault(name,name); } };
        Remapper symbols=new Remapper() {
            @Override public String map(String name) { return names.map(name); }
            @Override public String mapMethodName(String owner,String name,String descriptor) {
                return ownedMembers.getOrDefault("M\t"+names.map(owner)+"\t"+name+"\t"+names.mapMethodDesc(descriptor),members.getOrDefault("M\t"+name+"\t"+names.mapMethodDesc(descriptor),name));
            }
            @Override public String mapFieldName(String owner,String name,String descriptor) {
                return ownedMembers.getOrDefault("F\t"+names.map(owner)+"\t"+name+"\t"+names.mapDesc(descriptor),members.getOrDefault("F\t"+name+"\t"+names.mapDesc(descriptor),name));
            }
        };
        Map<String,TypeSpec> expected=new TreeMap<>();
        for (String line : Files.readAllLines(Path.of(args[3]))) {
            String[] row=line.split("\t",-1);TypeSpec spec=expected.computeIfAbsent(row[1],unused->new TypeSpec());
            switch(row[0]) {
                case "C": spec.type=row; break;
                case "M": spec.methods.put(row[2]+row[3],row); break;
                case "F": spec.fields.put(row[2]+row[3],row); break;
                case "E": spec.enclosing=row; break;
                case "I": spec.inners.add(row); break;
                default: break;
            }
        }
        Set<String> sourceHelperAllowlist=new HashSet<>();
        if(args.length>4)for(String line:Files.readAllLines(Path.of(args[4])))if(!line.isBlank()) {
            String[] row=line.split("\t",-1);
            if(row.length!=4 || !row[0].equals("M"))throw new IllegalStateException("Invalid exact source-helper declaration");
            sourceHelperAllowlist.add(row[1]+"\t"+row[2]+row[3]);
        }
        List<String> missing=new ArrayList<>(), extra=new ArrayList<>(), allowed=new ArrayList<>(), sourceHelpers=new ArrayList<>(), metadata=new ArrayList<>();
        Set<String> actualClasses=new TreeSet<>();
        Files.createDirectories(output);
        try (var paths=Files.walk(compiled)) {
            for (Path path : paths.filter(p->p.toString().endsWith(".class")).sorted().toList()) {
                ClassNode node=new ClassNode();
                new ClassReader(Files.readAllBytes(path)).accept(new ClassRemapper(node,symbols),0);
                // AAPT-generated R belongs to the resource build, not the vendor's original classes.jar.
                if (node.name.equals("io/intercom/android/sdk/ui/R") || node.name.startsWith("io/intercom/android/sdk/ui/R$")) continue;
                actualClasses.add(node.name);TypeSpec spec=expected.get(node.name);
                if (spec==null || spec.type==null) { extra.add("C\t"+node.name); continue; }
                String[] type=spec.type;
                List<String> interfaces=type[6].isEmpty()?List.of():Arrays.asList(type[6].split(","));
                if (!Objects.equals(node.superName,nullable(type[4])) || !new TreeSet<>(node.interfaces).equals(new TreeSet<>(interfaces))) {
                    throw new IllegalStateException("Source hierarchy differs for "+node.name);
                }
                node.version=Integer.parseInt(type[2]);node.access=Integer.parseInt(type[3]);node.signature=nullable(type[5]);
                for (MethodNode method : node.methods) for (AbstractInsnNode instruction : method.instructions) {
                    String owner=null;
                    if (instruction instanceof TypeInsnNode allocation && allocation.getOpcode()==Opcodes.NEW) owner=allocation.desc;
                    if (instruction instanceof MethodInsnNode call && call.name.equals("<init>")) owner=call.owner;
                    TypeSpec target=owner==null?null:expected.get(owner);
                    if (target!=null && target.methods.keySet().stream().noneMatch(key->key.startsWith("<init>"))) {
                        throw new IllegalStateException("Source references compiler-only constructor: "+owner);
                    }
                }
                Set<String> actualMethods=new HashSet<>(), actualFields=new HashSet<>();
                for (FieldNode field : node.fields) {
                    String key=field.name+field.desc;actualFields.add(key);String[] row=spec.fields.get(key);
                    if (row==null) { extra.add("F\t"+node.name+"\t"+key); continue; }
                    if (field.access!=Integer.parseInt(row[4])) metadata.add("F\t"+node.name+"\t"+key+"\t"+field.access+"->"+row[4]);
                    field.access=Integer.parseInt(row[4]);field.signature=nullable(row[5]);
                }
                for (Iterator<MethodNode> iterator=node.methods.iterator(); iterator.hasNext();) {
                    MethodNode method=iterator.next();
                    if (method.name.equals("<init>") && spec.methods.keySet().stream().noneMatch(key->key.startsWith("<init>"))) {
                        if (!emptyJavaConstructor(method)) throw new IllegalStateException("Refusing to remove non-empty source constructor: "+node.name+method.desc);
                        metadata.add("REMOVE_JAVA_IMPLICIT_CONSTRUCTOR\t"+node.name+"\t"+method.desc);
                        iterator.remove();
                        continue;
                    }
                    String key=method.name+method.desc;actualMethods.add(key);String[] row=spec.methods.get(key);
                    if (row==null) {
                        String record="M\t"+node.name+"\t"+key;
                        // javac emits forwarding implementation methods for explicit source lambdas.
                        // Their bodies are source-compiled; they are listed separately, never hidden.
                        if (method.name.startsWith("lambda$") && (method.access&Opcodes.ACC_SYNTHETIC)!=0 && (method.access&Opcodes.ACC_PRIVATE)!=0) allowed.add(record);
                        else if(sourceHelperAllowlist.contains(node.name+"\t"+key)) {
                            if(method.access!=(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC))throw new IllegalStateException("Source helper must be exact private static declaration: "+record);
                            sourceHelpers.add(record);
                        } else extra.add(record);
                        continue;
                    }
                    if (method.access!=Integer.parseInt(row[4])) metadata.add("M\t"+node.name+"\t"+key+"\t"+method.access+"->"+row[4]);
                    method.access=Integer.parseInt(row[4]);method.signature=nullable(row[5]);
                    method.exceptions=row[6].isEmpty()?new ArrayList<>():new ArrayList<>(Arrays.asList(row[6].split(",")));
                }
                for (String key : spec.fields.keySet()) if (!actualFields.contains(key)) missing.add("F\t"+node.name+"\t"+key);
                for (String key : spec.methods.keySet()) if (!actualMethods.contains(key)) missing.add("M\t"+node.name+"\t"+key);
                node.innerClasses.clear();
                for (String[] row : spec.inners) node.innerClasses.add(new InnerClassNode(row[2],nullable(row[3]),nullable(row[4]),Integer.parseInt(row[5])));
                if (spec.enclosing!=null) { node.outerClass=nullable(spec.enclosing[2]);node.outerMethod=nullable(spec.enclosing[3]);node.outerMethodDesc=nullable(spec.enclosing[4]); }
                else { node.outerClass=null;node.outerMethod=null;node.outerMethodDesc=null; }
                ClassWriter writer=new ClassWriter(0);node.accept(writer);
                Path target=output.resolve(node.name+".class");Files.createDirectories(target.getParent());Files.write(target,writer.toByteArray());
            }
        }
        for (String name : expected.keySet()) if (!actualClasses.contains(name)) missing.add("C\t"+name);
        Path reports=output.getParent();
        Files.write(reports.resolve("abi-missing.tsv"),missing);Files.write(reports.resolve("abi-extra.tsv"),extra);
        Files.write(reports.resolve("source-recovery-helper-methods.tsv"),sourceHelpers);
        if(sourceHelpers.size()!=sourceHelperAllowlist.size())throw new IllegalStateException("Allowlisted source helper missing");
        Files.write(reports.resolve("compiler-lambda-methods.tsv"),allowed);Files.write(reports.resolve("metadata-adjustments.tsv"),metadata);
        System.out.println("Classes="+actualClasses.size()+", expected="+expected.size()+", missing="+missing.size()+", extra="+extra.size()+", source-compiled lambda helpers="+allowed.size()+", explicit private source helpers="+sourceHelpers.size());
        if (!missing.isEmpty() || !extra.isEmpty()) throw new IllegalStateException("Incomplete ABI; see explicit reports");
    }
}
