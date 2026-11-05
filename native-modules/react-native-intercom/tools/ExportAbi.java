import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

// Export type metadata only; method instructions are never part of the source build specification.
public class ExportAbi {
    static String text(String value){return value==null?"":value;}
    public static void main(String[] args)throws Exception {
        List<String> lines=new ArrayList<>();
        for(int i=1;i<args.length;i++)try(ZipFile zip=new ZipFile(args[i])) {
            List<? extends ZipEntry> entries=Collections.list(zip.entries());entries.sort(Comparator.comparing(ZipEntry::getName));
            for(ZipEntry entry:entries)if(entry.getName().endsWith(".class")) {
                ClassNode node=new ClassNode();new ClassReader(zip.getInputStream(entry)).accept(node,ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
                lines.add(String.join("\t","C",node.name,""+node.version,""+node.access,text(node.superName),text(node.signature),String.join(",",node.interfaces)));
                if(node.outerClass!=null)lines.add(String.join("\t","E",node.name,node.outerClass,text(node.outerMethod),text(node.outerMethodDesc)));
                for(InnerClassNode inner:node.innerClasses)lines.add(String.join("\t","I",node.name,inner.name,text(inner.outerName),text(inner.innerName),""+inner.access));
                for(FieldNode f:node.fields)lines.add(String.join("\t","F",node.name,f.name,f.desc,""+f.access,text(f.signature)));
                for(MethodNode m:node.methods) {
                    lines.add(String.join("\t","M",node.name,m.name,m.desc,""+m.access,text(m.signature),String.join(",",m.exceptions)));
                    if((m.access&Opcodes.ACC_SYNTHETIC)!=0&&!m.name.startsWith("<")&&m.instructions.size()>0)lines.add(String.join("\t","P",node.name,m.name,m.desc,ArtifactFingerprint.of(m)));
                }
            }
        }
        Files.write(Path.of(args[0]),lines,StandardCharsets.UTF_8);System.out.println("Exported "+lines.size()+" ABI metadata records");
    }
}
