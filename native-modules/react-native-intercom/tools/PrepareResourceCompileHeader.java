import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Remove constant-folding attributes from caller-supplied AGP R declarations, never packaged. */
public final class PrepareResourceCompileHeader {
 public static void main(String[] args)throws Exception{int count=0;Set<String> required=new HashSet<>(List.of("intercom_chevron","intercom_conversation_card_question","intercom_send_message_icon"));try(ZipFile input=new ZipFile(args[0]);ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){for(ZipEntry entry:Collections.list(input.entries())){if(entry.isDirectory())continue;if(!entry.getName().endsWith(".class"))continue;ClassNode node=new ClassNode();new ClassReader(input.getInputStream(entry)).accept(node,0);if(!node.name.equals("io/intercom/android/sdk/R")&&!node.name.startsWith("io/intercom/android/sdk/R$"))throw new IllegalArgumentException("Only own AGP base R classes accepted");for(FieldNode field:node.fields){field.access&=~Opcodes.ACC_FINAL;field.value=null;if(node.name.equals("io/intercom/android/sdk/R$drawable")&&field.desc.equals("I"))required.remove(field.name);}ClassWriter writer=new ClassWriter(0);node.accept(writer);ZipEntry copy=new ZipEntry(entry.getName());copy.setTime(0);output.putNextEntry(copy);output.write(writer.toByteArray());output.closeEntry();count++;}}if(!required.isEmpty())throw new IllegalStateException("Required icon resources missing: "+required);System.out.println("Compile-only resource header classes="+count+"; fields nonfinal/no ConstantValue; excluded from SDK output");}
}
