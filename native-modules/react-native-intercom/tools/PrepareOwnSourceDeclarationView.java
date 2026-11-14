import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
/** Own source-compiled headers for javac8 cycle resolution; never a runtime artifact. */
public final class PrepareOwnSourceDeclarationView {
 public static void main(String[] args)throws Exception {
  Path source=Path.of(args[0]);int count=0;
  try(ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])));var paths=Files.walk(source)){
   for(Path p:paths.filter(x->x.toString().endsWith(".class")).sorted().toList()){
    ClassNode n=new ClassNode();new ClassReader(Files.readAllBytes(p)).accept(n,0);
    if(n.name.matches("io/intercom/android/sdk/blocks/lib/(BlockAlignment|BlockType)(\\$.*)?"))continue;
    n.version=Opcodes.V1_8;n.nestHostClass=null;n.nestMembers=null;n.permittedSubclasses=null;
    ClassWriter w=new ClassWriter(0);n.accept(w);ZipEntry e=new ZipEntry(n.name+".class");e.setTime(0);output.putNextEntry(e);output.write(w.toByteArray());output.closeEntry();count++;
   }
  }System.out.println("Own source output compile-only declaration view="+count+"; no runtime packaging");
 }
}
