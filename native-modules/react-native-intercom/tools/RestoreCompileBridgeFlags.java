import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.commons.Remapper;

/** Preserve third-party bridge visibility and the exact deprecated SupervisorJob overload in a Java-only compile view. */
public final class RestoreCompileBridgeFlags {
 public static void main(String[] args)throws Exception {
  Path rawRoot=Path.of(args[0]),viewRoot=Path.of(args[1]);Map<String,String> classes=new HashMap<>(),methods=new HashMap<>();List<String> ledger=new ArrayList<>();Set<String> legacyDefaults=new HashSet<>();
  String legacyOwner="kotlinx/coroutines/SupervisorKt",legacyName="SupervisorJob$default",legacyDesc="(Lkotlinx/coroutines/Job;ILjava/lang/Object;)Lkotlinx/coroutines/Job;",legacyPlainDesc="(Lkotlinx/coroutines/Job;)Lkotlinx/coroutines/Job;";
  for(String line:Files.readAllLines(Path.of(args[2]))){String[] row=line.split("\t");if(row[0].equals("C"))classes.put(row[1],row[2]);else if(row[0].equals("M"))methods.put(row[1]+"\t"+row[2]+row[3],row[4]);}
  Remapper remapper=new Remapper(){@Override public String map(String name){return classes.getOrDefault(name,name);}@Override public String mapMethodName(String owner,String name,String desc){return methods.getOrDefault(owner+"\t"+name+desc,name);}};
  List<Path> jars;try(var paths=Files.list(rawRoot)){jars=paths.filter(path->path.getFileName().toString().matches("dependency-[0-9]+\\.jar")).sorted().toList();}
  for(Path raw:jars){Path view=viewRoot.resolve(raw.getFileName());if(!Files.isRegularFile(view))continue;Map<String,Map<String,Integer>> originals=new HashMap<>();
   try(ZipFile archive=new ZipFile(raw.toFile())){for(ZipEntry entry:Collections.list(archive.entries()))if(entry.getName().endsWith(".class")){
    ClassNode node=new ClassNode();new ClassReader(archive.getInputStream(entry)).accept(node,ClassReader.SKIP_CODE|ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);
    if(node.name.startsWith("io/intercom/android/")||node.name.startsWith("com/intercom/twig/"))throw new IllegalStateException("SDK implementation cannot be an external compile-view input: "+raw);
    if(node.name.equals(legacyOwner)){
     for(String[] pair:List.of(new String[]{legacyName,legacyDesc,"(Lkotlinx/coroutines/Job;ILjava/lang/Object;)Lkotlinx/coroutines/CompletableJob;"},new String[]{"SupervisorJob",legacyPlainDesc,"(Lkotlinx/coroutines/Job;)Lkotlinx/coroutines/CompletableJob;"})){
      MethodNode legacy=node.methods.stream().filter(m->m.name.equals(pair[0])&&m.desc.equals(pair[1])).findFirst().orElseThrow();
      MethodNode current=node.methods.stream().filter(m->m.name.equals(pair[0])&&m.desc.equals(pair[2])).findFirst().orElseThrow();
      if((legacy.access&(Opcodes.ACC_SYNTHETIC|Opcodes.ACC_BRIDGE|Opcodes.ACC_DEPRECATED))!=(Opcodes.ACC_SYNTHETIC|Opcodes.ACC_DEPRECATED)||((current.access&Opcodes.ACC_SYNTHETIC)!=0)!=pair[0].equals(legacyName))throw new IllegalStateException("Frozen SupervisorJob overload metadata mismatch");
      if(!legacyDefaults.add(node.name+"\t"+legacy.name+legacy.desc))throw new IllegalStateException("Duplicate SupervisorJob declaration");
     }
    }
    for(MethodNode method:node.methods)if((method.access&Opcodes.ACC_BRIDGE)!=0||(node.name.equals(legacyOwner)&&((method.name.equals(legacyName)&&method.desc.equals(legacyDesc))||(method.name.equals("SupervisorJob")&&method.desc.equals(legacyPlainDesc)))))originals.computeIfAbsent(remapper.mapType(node.name),ignored->new HashMap<>()).put(remapper.mapMethodName(node.name,method.name,method.desc)+remapper.mapMethodDesc(method.desc),method.access);
   }}
   Path temporary=view.resolveSibling(view.getFileName()+".bridge-flags.tmp");boolean changed=false;
   try(ZipFile archive=new ZipFile(view.toFile());ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(temporary))){for(ZipEntry entry:Collections.list(archive.entries())){
    if(entry.isDirectory())continue;byte[] data=archive.getInputStream(entry).readAllBytes();boolean classChanged=false;
    if(entry.getName().endsWith(".class")){ClassNode node=new ClassNode();new ClassReader(data).accept(node,0);
     for(MethodNode method:node.methods){Integer original=originals.getOrDefault(node.name,Map.of()).get(method.name+method.desc);if(original==null)continue;int before=method.access;method.access=(before&~(Opcodes.ACC_BRIDGE|Opcodes.ACC_SYNTHETIC))|(original&(Opcodes.ACC_BRIDGE|Opcodes.ACC_SYNTHETIC));
      if(before!=method.access){classChanged=true;ledger.add(String.join("\t",raw.getFileName().toString(),node.name,method.name,method.desc,""+before,""+method.access));}
     }if(classChanged){ClassWriter writer=new ClassWriter(0);node.accept(writer);data=writer.toByteArray();changed=true;}
    }
    ZipEntry copy=new ZipEntry(entry.getName());copy.setTime(entry.getTime());output.putNextEntry(copy);output.write(data);output.closeEntry();
   }}if(changed)Files.move(temporary,view,StandardCopyOption.REPLACE_EXISTING);else Files.delete(temporary);
  }
  if(legacyDefaults.size()!=2)throw new IllegalStateException("Frozen SupervisorJob declaration absent");
  Files.write(Path.of(args[3]),ledger);System.out.println("Restored original third-party bridge flag rows="+ledger.size());
 }
}
