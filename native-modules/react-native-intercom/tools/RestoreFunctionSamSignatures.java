import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Restore only the Java-valid generic SAM declarations needed for original typed callbacks. */
public final class RestoreFunctionSamSignatures {
 public static void main(String[] args)throws Exception {
  Map<String,String> expected=Map.of("kotlin/Function","<R:Ljava/lang/Object;>Ljava/lang/Object;","kotlin/jvm/functions/Function0","<R:Ljava/lang/Object;>Ljava/lang/Object;Lkotlin/Function<TR;>;","kotlin/jvm/functions/Function1","<P1:Ljava/lang/Object;R:Ljava/lang/Object;>Ljava/lang/Object;Lkotlin/Function<TR;>;","androidx/activity/result/ActivityResultCallback","<O:Ljava/lang/Object;>Ljava/lang/Object;","android/webkit/ValueCallback","<T:Ljava/lang/Object;>Ljava/lang/Object;");List<String> rows=new ArrayList<>();int found=0;List<Path> jars;
  try(var paths=Files.list(Path.of(args[0]))){jars=paths.filter(p->(p.getFileName().toString().matches("dependency-[0-9]+\\.jar")||p.getFileName().toString().equals("android.jar"))).sorted().toList();}
  for(Path raw:jars){Map<String,ClassNode> declarations=new HashMap<>();try(ZipFile input=new ZipFile(raw.toFile())){for(String owner:expected.keySet()){ZipEntry entry=input.getEntry(owner+".class");if(entry==null)continue;ClassNode node=new ClassNode();new ClassReader(input.getInputStream(entry)).accept(node,ClassReader.SKIP_CODE|ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);if(!expected.get(owner).equals(node.signature))throw new IllegalStateException("Unsupported SAM class signature: "+owner);declarations.put(owner,node);found++;}}
   if(declarations.isEmpty())continue;Path view=Path.of(args[1]).resolve(raw.getFileName()),temporary=view.resolveSibling(view.getFileName()+".sam-signatures.tmp");boolean changed=false;
   try(ZipFile input=new ZipFile(view.toFile());ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(temporary))){for(ZipEntry entry:Collections.list(input.entries())){if(entry.isDirectory())continue;byte[] data=input.getInputStream(entry).readAllBytes();String owner=entry.getName().replaceFirst("\\.class$","");ClassNode original=declarations.get(owner);
    if(original!=null){ClassNode node=new ClassNode();new ClassReader(data).accept(node,0);boolean classChanged=false;if(!Objects.equals(node.signature,original.signature)){rows.add(String.join("\t",raw.getFileName().toString(),owner,"CLASS",String.valueOf(node.signature),original.signature));node.signature=original.signature;classChanged=true;}
     if(!owner.equals("kotlin/Function")){String name=owner.startsWith("kotlin/jvm/functions/Function")?"invoke":owner.equals("android/webkit/ValueCallback")?"onReceiveValue":"onActivityResult",desc=owner.equals("kotlin/jvm/functions/Function0")?"()Ljava/lang/Object;":owner.equals("kotlin/jvm/functions/Function1")?"(Ljava/lang/Object;)Ljava/lang/Object;":"(Ljava/lang/Object;)V",signature=owner.equals("kotlin/jvm/functions/Function0")?"()TR;":owner.equals("kotlin/jvm/functions/Function1")?"(TP1;)TR;":owner.equals("android/webkit/ValueCallback")?"(TT;)V":"(TO;)V";List<MethodNode> methods=original.methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(desc)).toList();if(methods.size()!=1||!Objects.equals(methods.get(0).signature,signature))throw new IllegalStateException("Unsupported SAM invoke signature");List<MethodNode> targets=node.methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(desc)).toList();if(targets.size()!=1)throw new IllegalStateException("SAM invoke missing");MethodNode method=targets.get(0);if(!Objects.equals(method.signature,methods.get(0).signature)){rows.add(String.join("\t",raw.getFileName().toString(),owner,method.name+method.desc,String.valueOf(method.signature),methods.get(0).signature));method.signature=methods.get(0).signature;classChanged=true;}}
     if(classChanged){ClassWriter writer=new ClassWriter(0);node.accept(writer);data=writer.toByteArray();changed=true;}
    }ZipEntry copy=new ZipEntry(entry.getName());copy.setTime(entry.getTime());output.putNextEntry(copy);output.write(data);output.closeEntry();
   }}if(changed)Files.move(temporary,view,StandardCopyOption.REPLACE_EXISTING);else Files.delete(temporary);
  }
  if(found!=5)throw new IllegalStateException("Expected exactly five frozen SAM declarations, found "+found);Files.write(Path.of(args[2]),rows);System.out.println("Restored exact SAM Signature attributes="+rows.size());
 }
}
