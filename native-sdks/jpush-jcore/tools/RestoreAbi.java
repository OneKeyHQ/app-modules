import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.*;

// Compile from Java plus explicit ABI metadata. Never copy original SDK instructions into the build.
public class RestoreAbi {
    static class Spec {
        String[] type,enclosing;
        final Map<String,String[]> methods=new TreeMap<>(),fields=new TreeMap<>();
        final Map<String,String> fingerprints=new TreeMap<>();
        final List<String[]> inners=new ArrayList<>();
    }
    static String nullable(String value){return value.isEmpty()?null:value;}
    static String profile(ClassNode node) {
        List<String> methods=new ArrayList<>();for(MethodNode method:node.methods)if((method.access&Opcodes.ACC_PUBLIC)!=0&&!method.name.startsWith("<"))methods.add(method.name+method.desc);
        Collections.sort(methods);List<String> interfaces=new ArrayList<>(node.interfaces);Collections.sort(interfaces);return node.superName+"|"+interfaces+"|"+methods;
    }
    static String profile(Spec spec) {
        List<String> methods=new ArrayList<>();for(String[] method:spec.methods.values())if((Integer.parseInt(method[4])&Opcodes.ACC_PUBLIC)!=0&&!method[2].startsWith("<"))methods.add(method[2]+method[3]);
        Collections.sort(methods);List<String> interfaces=new ArrayList<>(spec.type[6].isEmpty()?List.of():Arrays.asList(spec.type[6].split(",")));Collections.sort(interfaces);return spec.type[4]+"|"+interfaces+"|"+methods;
    }
    public static void main(String[] args)throws Exception {
        Path source=Path.of(args[0]),out=Path.of(args[1]);Files.createDirectories(out);
        Map<String,String> inverse=new HashMap<>();
        for(String line:Files.readAllLines(Path.of(args[2]))) {
            String[] p=line.split("\t",-1);if(p[0].equals("C"))inverse.put(p[2],p[1]);
        }
        Map<String,Spec> specs=new TreeMap<>();
        for(String line:Files.readAllLines(Path.of(args[3]))) {
            String[] p=line.split("\t",-1);Spec s=specs.computeIfAbsent(p[1],k->new Spec());
            switch(p[0]) {
                case "C":s.type=p;break;
                case "F":s.fields.put(p[2]+p[3],p);break;
                case "M":s.methods.put(p[2]+p[3],p);break;
                case "E":s.enclosing=p;break;
                case "I":s.inners.add(p);break;
                case "P":s.fingerprints.put(p[2]+p[3],p[4]);break;
            }
        }
        Set<String> normalized=new HashSet<>(inverse.keySet());
        Remapper remapper=new Remapper() {
            @Override public String map(String name){return inverse.getOrDefault(name,name);}
            @Override public String mapFieldName(String owner,String name,String descriptor){return normalized.contains(owner)&&name.startsWith("recoverField_")?name.substring(13):name;}
            @Override public String mapMethodName(String owner,String name,String descriptor){
                if(normalized.contains(owner)&&name.startsWith("recoverMethod_"))return name.substring(14,name.lastIndexOf('_'));
                return name;
            }
        };
        Map<String,ClassNode> rebuilt=new TreeMap<>();
        try(var paths=Files.walk(source)) {
            for(Path path:paths.filter(p->p.toString().endsWith(".class")).sorted().toList()) {
                ClassNode node=new ClassNode();new ClassReader(Files.readAllBytes(path)).accept(new ClassRemapper(node,remapper),0);rebuilt.put(node.name,node);
            }
        }
        // Reviewed identities follow the original method bodies, not source declaration order.
        Map<String,String> anonymousNames=new TreeMap<>();
        Path identityFile=Path.of(args[3]).getParent().resolve("compiler-identities.tsv");
        if(Files.exists(identityFile))for(String line:Files.readAllLines(identityFile)) {
            String[] parts=line.split("\t");if(parts.length==2)anonymousNames.put(parts[0],parts[1]);
        }
        if(!anonymousNames.isEmpty()) {
            Map<String,String> expanded=new TreeMap<>();
            for(String name:rebuilt.keySet()) {
                String mapped=name;
                for(var entry:anonymousNames.entrySet())if(name.equals(entry.getKey())||name.startsWith(entry.getKey()+"$")){mapped=entry.getValue()+name.substring(entry.getKey().length());break;}
                expanded.put(name,mapped);
            }
            if(new HashSet<>(expanded.values()).size()!=expanded.size())throw new IllegalStateException("Ambiguous anonymous class numbering: "+anonymousNames+" expanded="+expanded.entrySet().stream().filter(e->!e.getKey().equals(e.getValue())).toList());
            Map<String,ClassNode> remapped=new TreeMap<>();Remapper identity=new Remapper(){@Override public String map(String name){return expanded.getOrDefault(name,name);}};
            for(ClassNode node:rebuilt.values()){ClassNode target=new ClassNode();node.accept(new ClassRemapper(target,identity));remapped.put(target.name,target);}rebuilt=remapped;
            List<String> aliases=new ArrayList<>();for(var entry:anonymousNames.entrySet())aliases.add(entry.getKey()+"\t"+entry.getValue());Files.write(out.resolveSibling("anonymous-class-identities.tsv"),aliases);
        }
        Map<String,String> capturedFields=new TreeMap<>();
        Path fieldIdentities=Path.of(args[3]).getParent().resolve("compiler-fields.tsv");
        if(Files.exists(fieldIdentities))for(String line:Files.readAllLines(fieldIdentities)) {
            String[] p=line.split("\t");capturedFields.put(p[0]+"/"+p[1]+p[2],p[3]);
        }
        for(ClassNode node:rebuilt.values()) {
            Spec spec=specs.get(node.name);if(spec==null)continue;
            Set<String> present=new HashSet<>();for(FieldNode field:node.fields)present.add(field.name+field.desc);
            Map<String,List<String[]>> missingByType=new TreeMap<>();
            for(String[] field:spec.fields.values())if(!present.contains(field[2]+field[3])&&(Integer.parseInt(field[4])&Opcodes.ACC_SYNTHETIC)!=0)missingByType.computeIfAbsent(field[3],k->new ArrayList<>()).add(field);
            for(var entry:missingByType.entrySet()) {
                List<FieldNode> extra=new ArrayList<>();for(FieldNode field:node.fields)if(field.desc.equals(entry.getKey())&&!spec.fields.containsKey(field.name+field.desc))extra.add(field);
                if(entry.getValue().size()==1&&extra.size()==1)capturedFields.put(node.name+"/"+extra.get(0).name+extra.get(0).desc,entry.getValue().get(0)[2]);
            }
        }
        if(!capturedFields.isEmpty()) {
            Remapper fields=new Remapper(){@Override public String mapFieldName(String owner,String name,String descriptor){return capturedFields.getOrDefault(owner+"/"+name+descriptor,name);}};
            Map<String,ClassNode> adjusted=new TreeMap<>();for(ClassNode node:rebuilt.values()){ClassNode target=new ClassNode();node.accept(new ClassRemapper(target,fields));adjusted.put(target.name,target);}rebuilt=adjusted;
        }
        Map<String,String> accessors=new TreeMap<>();
        for(int round=0;round<10;round++) {
            Map<String,String> next=new TreeMap<>();
            for(ClassNode node:rebuilt.values()) {
                Spec spec=specs.get(node.name);if(spec==null)continue;
                Set<String> present=new HashSet<>();for(MethodNode method:node.methods)present.add(method.name+method.desc);
                for(MethodNode method:node.methods) {
                    if(!method.name.startsWith("access$")&&!method.name.startsWith("lambda$"))continue;
                    String hash=ArtifactFingerprint.of(method);List<String> matches=new ArrayList<>();
                    for(var entry:spec.fingerprints.entrySet())if(!present.contains(entry.getKey())&&entry.getKey().endsWith(method.desc)&&entry.getValue().equals(hash))matches.add(entry.getKey());
                    if(matches.size()==1)next.put(node.name+"/"+method.name+method.desc,matches.get(0).substring(0,matches.get(0).indexOf('(')));
                }
            }
            if(next.isEmpty())break;accessors.putAll(next);
            Remapper methods=new Remapper(){@Override public String mapMethodName(String owner,String name,String descriptor){return next.getOrDefault(owner+"/"+name+descriptor,name);}};
            Map<String,ClassNode> adjusted=new TreeMap<>();for(ClassNode node:rebuilt.values()){ClassNode target=new ClassNode();node.accept(new ClassRemapper(target,methods));adjusted.put(target.name,target);}rebuilt=adjusted;
        }
        List<String> artifacts=new ArrayList<>();for(var entry:capturedFields.entrySet())artifacts.add("F\t"+entry.getKey()+"\t"+entry.getValue());for(var entry:accessors.entrySet())artifacts.add("M\t"+entry.getKey()+"\t"+entry.getValue());Files.write(out.resolveSibling("compiler-artifact-restorations.tsv"),artifacts);
        int generatedMarkers=0,removedSourceConstructors=0;
        for(var entry:specs.entrySet())if(!rebuilt.containsKey(entry.getKey())&&entry.getValue().methods.isEmpty()&&entry.getValue().fields.isEmpty()) {
            String[] p=entry.getValue().type;ClassNode n=new ClassNode();n.visit(Integer.parseInt(p[2]),Integer.parseInt(p[3]),p[1],nullable(p[5]),nullable(p[4]),p[6].isEmpty()?null:p[6].split(","));rebuilt.put(n.name,n);generatedMarkers++;
        }
        List<String> missing=new ArrayList<>(),extras=new ArrayList<>(),metadata=new ArrayList<>();
        for(var entry:rebuilt.entrySet()) {
            ClassNode node=entry.getValue();Spec s=specs.get(node.name);
            if(s==null){extras.add("C\t"+node.name);continue;}
            node.access=Integer.parseInt(s.type[3]);node.signature=nullable(s.type[5]);
            for(FieldNode f:node.fields) {
                String[] p=s.fields.get(f.name+f.desc);
                if(p!=null){f.access=Integer.parseInt(p[4]);f.signature=nullable(p[5]);}
            }
            Iterator<MethodNode> iterator=node.methods.iterator();
            while(iterator.hasNext()) {
                MethodNode m=iterator.next();String[] p=s.methods.get(m.name+m.desc);
                if(p==null&&m.name.equals("<init>")&&s.methods.keySet().stream().noneMatch(k->k.startsWith("<init>"))){metadata.add("REMOVE_SOURCE_CONSTRUCTOR\t"+node.name+"\t"+m.desc);iterator.remove();removedSourceConstructors++;continue;}
                if(p!=null){m.access=Integer.parseInt(p[4]);m.signature=nullable(p[5]);m.exceptions=p[6].isEmpty()?new ArrayList<>():new ArrayList<>(Arrays.asList(p[6].split(",")));}
            }
            node.innerClasses.clear();for(String[] p:s.inners)node.innerClasses.add(new InnerClassNode(p[2],nullable(p[3]),nullable(p[4]),Integer.parseInt(p[5])));
            if(s.enclosing!=null){node.outerClass=s.enclosing[2];node.outerMethod=nullable(s.enclosing[3]);node.outerMethodDesc=nullable(s.enclosing[4]);}
        }
        for(var entry:specs.entrySet()) {
            String name=entry.getKey();Spec s=entry.getValue();ClassNode n=rebuilt.get(name);
            if(n==null){missing.add("C\t"+name);continue;}
            Set<String> methods=new HashSet<>();for(MethodNode m:n.methods)methods.add(m.name+m.desc);
            Set<String> fields=new HashSet<>();for(FieldNode f:n.fields)fields.add(f.name+f.desc);
            for(String method:s.methods.keySet())if(!methods.contains(method))missing.add("M\t"+name+"\t"+method);
            for(String field:s.fields.keySet())if(!fields.contains(field))missing.add("F\t"+name+"\t"+field);
            for(String method:methods)if(!s.methods.containsKey(method))extras.add("M\t"+name+"\t"+method);
            for(String field:fields)if(!s.fields.containsKey(field))extras.add("F\t"+name+"\t"+field);
        }
        // Detect an invalid reference to a source-only constructor before removing it from the artifact.
        for(ClassNode n:rebuilt.values())for(MethodNode m:n.methods)for(AbstractInsnNode instruction:m.instructions)if(instruction instanceof MethodInsnNode call && call.name.equals("<init>")&&specs.containsKey(call.owner)&&specs.get(call.owner).methods.keySet().stream().noneMatch(k->k.startsWith("<init>")))throw new IllegalStateException("Reference to optimizer-pruned constructor "+call.owner+call.desc);
        for(ClassNode node:rebuilt.values()) {
            ClassWriter writer=new ClassWriter(0);node.accept(writer);Path target=out.resolve(node.name+".class");Files.createDirectories(target.getParent());Files.write(target,writer.toByteArray());
        }
        Files.write(out.resolveSibling("abi-missing.tsv"),missing);Files.write(out.resolveSibling("abi-extra.tsv"),extras);Files.write(out.resolveSibling("abi-metadata-adjustments.tsv"),metadata);
        System.out.println("Rebuilt classes="+rebuilt.size()+", markers="+generatedMarkers+", removed source-only constructors="+removedSourceConstructors+", missing ABI records="+missing.size()+", extra records="+extras.size());
    }
}
