import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Name one Java-inexpressible external return-only overload in a declaration-only compile view. */
public final class PrepareRememberSaveableCompileAlias {
 public static void main(String[] args)throws Exception {
  String owner="androidx/compose/runtime/saveable/RememberSaveableKt",name="rememberSaveable",alias="sourceRememberSaveableMutableState";
  String parameters="([Ljava/lang/Object;Landroidx/compose/runtime/saveable/Saver;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)",descriptor=parameters+"Landroidx/compose/runtime/MutableState;";
  String row=String.join("\t","M",owner,name,descriptor,alias);if(Files.readAllLines(Path.of(args[2])).stream().filter(row::equals).count()!=1)throw new IllegalStateException("Exact reversible alias absent");
  List<String> ledger=new ArrayList<>();int found=0;List<Path> jars;try(var paths=Files.list(Path.of(args[0]))){jars=paths.filter(p->p.getFileName().toString().matches("dependency-[0-9]+\\.jar")).sorted().toList();}
  for(Path raw:jars){try(ZipFile input=new ZipFile(raw.toFile())){ZipEntry entry=input.getEntry(owner+".class");if(entry==null)continue;found++;ClassNode original=new ClassNode();new ClassReader(input.getInputStream(entry)).accept(original,ClassReader.SKIP_CODE|ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
   for(String result:List.of("Landroidx/compose/runtime/MutableState;","Ljava/lang/Object;")){List<MethodNode> methods=original.methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(parameters+result)).toList();if(methods.size()!=1||methods.get(0).access!=(Opcodes.ACC_PUBLIC|Opcodes.ACC_STATIC|Opcodes.ACC_FINAL))throw new IllegalStateException("Frozen rememberSaveable overload mismatch");}
   Path view=Path.of(args[1]).resolve(raw.getFileName()),temporary=view.resolveSibling(view.getFileName()+".return-alias.tmp");boolean changed=false;
   try(ZipFile source=new ZipFile(view.toFile());ZipOutputStream target=new ZipOutputStream(Files.newOutputStream(temporary))){for(ZipEntry item:Collections.list(source.entries())){if(item.isDirectory())continue;byte[] bytes=source.getInputStream(item).readAllBytes();if(item.getName().equals(owner+".class")){ClassNode node=new ClassNode();new ClassReader(bytes).accept(node,0);List<MethodNode> methods=node.methods.stream().filter(m->m.desc.equals(descriptor)&&(m.name.equals(name)||m.name.equals(alias))).toList();if(methods.size()!=1)throw new IllegalStateException("Compile alias collision/missing target");MethodNode method=methods.get(0);if(method.name.equals(name)){method.name=alias;ClassWriter writer=new ClassWriter(0);node.accept(writer);bytes=writer.toByteArray();changed=true;ledger.add(raw.getFileName()+"\t"+row);}}
    ZipEntry copy=new ZipEntry(item.getName());copy.setTime(item.getTime());target.putNextEntry(copy);target.write(bytes);target.closeEntry();
   }}if(changed)Files.move(temporary,view,StandardCopyOption.REPLACE_EXISTING);else Files.delete(temporary);
  }}if(found!=1)throw new IllegalStateException("Expected one frozen rememberSaveable owner");Files.write(Path.of(args[3]),ledger);System.out.println("Exact external return-only declaration alias rows="+ledger.size());
 }
}
