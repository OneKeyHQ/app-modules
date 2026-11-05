import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Expose real linked transitive R declarations without inlining diagnostic consumer resource IDs. */
public final class PrepareResourceSymbols {
 public static void main(String[] args)throws Exception {
  int classes=0,fields=0;
  try(ZipFile source=new ZipFile(args[0]);ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){
   for(ZipEntry entry:Collections.list(source.entries()))if(entry.getName().matches("androidx/compose/ui/R(?:\\$[^/]+)?\\.class")){
    ClassNode node=new ClassNode();new ClassReader(source.getInputStream(entry)).accept(node,0);
    for(FieldNode field:node.fields){field.access&=~Opcodes.ACC_FINAL;field.value=null;fields++;}
    ClassWriter writer=new ClassWriter(0);node.accept(writer);ZipEntry replacement=new ZipEntry(entry.getName());replacement.setTime(0);output.putNextEntry(replacement);output.write(writer.toByteArray());output.closeEntry();classes++;
   }
  }
  if(classes==0)throw new IllegalStateException("No real Compose UI R symbols found");System.out.println("Compile-only actual R declarations: classes="+classes+", non-final fields="+fields+"; never package this JAR");
 }
}
