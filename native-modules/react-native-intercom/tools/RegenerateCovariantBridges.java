import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;
import org.objectweb.asm.Type;

/** Request javac bridges only after matching an exact original pure forwarder and source body. */
public final class RegenerateCovariantBridges {
 static String simple(String type){return type.substring(type.lastIndexOf('.')+1);}
 static boolean signature(MethodTree method,String[] proof) {
  if(!method.getName().contentEquals(proof[1])||method.getReturnType()==null||!simple(method.getReturnType().toString()).equals(simple(Type.getReturnType(proof[2]).getClassName())))return false;
  Type[] parameters=Type.getArgumentTypes(proof[2]);if(parameters.length!=method.getParameters().size())return false;
  for(int i=0;i<parameters.length;i++)if(!simple(method.getParameters().get(i).getType().toString()).equals(simple(parameters[i].getClassName())))return false;
  return true;
 }
 static boolean forwarder(MethodTree method,String[] proof) {
  if(method.getBody()==null||method.getBody().getStatements().size()!=1||!(method.getBody().getStatements().get(0)instanceof ReturnTree returned)||!(returned.getExpression()instanceof MethodInvocationTree call))return false;
  if(!(call.getMethodSelect()instanceof MemberSelectTree selected)||!selected.getExpression().toString().equals("this")||!selected.getIdentifier().contentEquals(proof[1]))return false;
  if(call.getArguments().size()!=method.getParameters().size())return false;
  for(int i=0;i<call.getArguments().size();i++)if(!(call.getArguments().get(i)instanceof IdentifierTree argument)||!argument.getName().equals(method.getParameters().get(i).getName()))return false;
  return true;
 }
 public static void main(String[] args)throws Exception {
  Map<String,List<String[]>> proofs=new HashMap<>();Set<String> matched=new HashSet<>();
  for(String line:Files.readAllLines(Path.of(args[0]))){String[] proof=line.split("\t");proofs.computeIfAbsent(proof[0],ignored->new ArrayList<>()).add(proof);}
  Path root=Path.of(args[1]);List<java.io.File> files=new ArrayList<>();try(var paths=Files.walk(root)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->files.add(path.toFile()));}
  List<String> edits=new ArrayList<>();JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
  try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
   DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
   for(CompilationUnitTree unit:task.parse()) {
    String file=Path.of(unit.getSourceFile().toUri()).toString();SourcePositions positions=Trees.instance(task).getSourcePositions();
    new TreePathScanner<Void,Void>() {
     @Override public Void visitMethod(MethodTree method,Void unused) {
      Tree owner=getCurrentPath().getParentPath().getLeaf();if(!(owner instanceof ClassTree clazz)||clazz.getSimpleName().isEmpty())return super.visitMethod(method,unused);
      String name=unit.getPackageName().toString().replace('.','/')+"/"+clazz.getSimpleName();
      for(String[] proof:proofs.getOrDefault(name,List.of()))if(signature(method,proof)) {
       String key=String.join("\t",Arrays.copyOf(proof,4));
       if(!forwarder(method,proof))throw new IllegalStateException("Original bridge source is not the proved pure forwarding body: "+key);
       if(!matched.add(key))throw new IllegalStateException("Ambiguous source bridge: "+key);
       edits.add(String.join("\t",file,""+positions.getStartPosition(unit,method),""+positions.getEndPosition(unit,method),key));
      }
      return super.visitMethod(method,unused);
     }
    }.scan(unit,null);
   }
   long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();if(errors!=0)throw new IllegalStateException("Parse errors: "+errors);
  }
  List<String> missing=new ArrayList<>();for(List<String[]> group:proofs.values())for(String[] proof:group){String key=String.join("\t",Arrays.copyOf(proof,4));if(!matched.contains(key))missing.add(key);}
  Collections.sort(edits);Collections.sort(missing);Files.write(Path.of(args[2]),edits);Files.write(Path.of(args[2]+".unmatched.tsv"),missing);System.out.println("Pure source bridges requested from javac "+edits.size()+", already absent/unmatched "+missing.size());
 }
}
