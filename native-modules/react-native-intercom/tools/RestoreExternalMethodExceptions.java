import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Restore an explicit finite list of original external checked-exception declarations. */
public final class RestoreExternalMethodExceptions {
 public static void main(String[] args)throws Exception{Map<String,Map<String,List<String>>> expected=new HashMap<>();List<String> ledger=new ArrayList<>();Set<String> found=new HashSet<>();
  for(String line:Files.readAllLines(Path.of(args[2]))){String[] row=line.split("\t",-1);if(row.length!=4)throw new IllegalArgumentException("Exception metadata row");expected.computeIfAbsent(row[0],key->new HashMap<>()).put(row[1]+row[2],row[3].isEmpty()?List.of():List.of(row[3].split(",")));}
  List<Path> jars;try(var paths=Files.list(Path.of(args[0]))){jars=paths.filter(p->(p.getFileName().toString().matches("dependency-[0-9]+\\.jar")||p.getFileName().toString().equals("android.jar"))).sorted().toList();}
  for(Path raw:jars){Set<String> selected=new HashSet<>();try(ZipFile input=new ZipFile(raw.toFile())){for(String owner:expected.keySet()){ZipEntry entry=input.getEntry(owner+".class");if(entry==null)continue;ClassNode node=new ClassNode();new ClassReader(input.getInputStream(entry)).accept(node,ClassReader.SKIP_CODE|ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES);for(var method:expected.get(owner).entrySet()){List<MethodNode> matching=node.methods.stream().filter(m->(m.name+m.desc).equals(method.getKey())).toList();if(matching.size()!=1||!matching.get(0).exceptions.equals(method.getValue()))throw new IllegalStateException("Frozen external Exceptions mismatch: "+owner+method.getKey());if(!found.add(owner+"\t"+method.getKey()))throw new IllegalStateException("Duplicate external declaration");}selected.add(owner);}}
   if(selected.isEmpty())continue;Path view=Path.of(args[1]).resolve(raw.getFileName()),temporary=view.resolveSibling(view.getFileName()+".exceptions.tmp");boolean changed=false;
   try(ZipFile input=new ZipFile(view.toFile());ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(temporary))){for(ZipEntry entry:Collections.list(input.entries())){if(entry.isDirectory())continue;byte[] data=input.getInputStream(entry).readAllBytes();String owner=entry.getName().replaceFirst("\\.class$","");if(selected.contains(owner)){ClassNode node=new ClassNode();new ClassReader(data).accept(node,0);boolean classChanged=false;for(var method:expected.get(owner).entrySet()){List<MethodNode> matching=node.methods.stream().filter(m->(m.name+m.desc).equals(method.getKey())).toList();if(matching.size()!=1)throw new IllegalStateException("Compile method missing");MethodNode target=matching.get(0);if(!target.exceptions.equals(method.getValue())){ledger.add(String.join("\t",raw.getFileName().toString(),owner,method.getKey(),String.join(",",target.exceptions),String.join(",",method.getValue())));target.exceptions=new ArrayList<>(method.getValue());classChanged=true;}}if(classChanged){ClassWriter writer=new ClassWriter(0);node.accept(writer);data=writer.toByteArray();changed=true;}}
    ZipEntry copy=new ZipEntry(entry.getName());copy.setTime(entry.getTime());output.putNextEntry(copy);output.write(data);output.closeEntry();
   }}if(changed)Files.move(temporary,view,StandardCopyOption.REPLACE_EXISTING);else Files.delete(temporary);
  }
  if(found.size()!=expected.values().stream().mapToInt(Map::size).sum())throw new IllegalStateException("Original external exception declaration absent");Files.write(Path.of(args[3]),ledger);System.out.println("Restored finite external Exceptions attributes="+ledger.size());
 }
}
