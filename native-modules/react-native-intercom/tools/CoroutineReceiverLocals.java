import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Preserve coroutine slot-zero reassignments as an explicit mutable source local. */
public final class CoroutineReceiverLocals {
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);List<java.io.File> files=new ArrayList<>();
        try(var paths=Files.walk(root)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->files.add(path.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();List<String> rows=new ArrayList<>();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
            JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            for(CompilationUnitTree unit:task.parse()) {
                String file=Path.of(unit.getSourceFile().toUri()).toString();SourcePositions positions=Trees.instance(task).getSourcePositions();
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitMethod(MethodTree method,Void unused) {
                        if(method.getBody()==null || method.getName().contentEquals("<init>"))return super.visitMethod(method,unused);
                        boolean[] assigns={false};List<IdentifierTree> receivers=new ArrayList<>();
                        new TreeScanner<Void,Void>() {
                            @Override public Void visitClass(ClassTree nested,Void ignored){return null;}
                            @Override public Void visitAssignment(AssignmentTree assignment,Void ignored) {
                                if(assignment.getVariable() instanceof IdentifierTree identifier && identifier.getName().contentEquals("this"))assigns[0]=true;
                                return super.visitAssignment(assignment,ignored);
                            }
                            @Override public Void visitIdentifier(IdentifierTree identifier,Void ignored) {
                                if(identifier.getName().contentEquals("this"))receivers.add(identifier);
                                if(identifier.getName().contentEquals("recoveredThis"))throw new IllegalStateException("Recovery-local name collision");return null;
                            }
                        }.scan(method.getBody(),null);
                        if(assigns[0]) {
                            if(method.getModifiers().getFlags().contains(javax.lang.model.element.Modifier.STATIC))throw new IllegalStateException("Static receiver reassignment");
                            TreePath owner=getCurrentPath().getParentPath();if(!(owner.getLeaf() instanceof ClassTree clazz))throw new IllegalStateException("Unresolved owner");
                            long body=positions.getStartPosition(unit,method.getBody());rows.add(String.join("\t",file,""+(body+1),""+(body+1),"\n      "+clazz.getSimpleName()+" recoveredThis = this;","DECLARE",method.getName().toString()));
                            for(IdentifierTree receiver:receivers)rows.add(String.join("\t",file,""+positions.getStartPosition(unit,receiver),""+positions.getEndPosition(unit,receiver),"recoveredThis","RECEIVER",method.getName().toString()));
                        }
                        return super.visitMethod(method,unused);
                    }
                }.scan(unit,null);
            }
            long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();if(errors!=0)throw new IllegalStateException("Parse errors: "+errors);
        }
        // Escape inserted newlines so the proof remains one edit per TSV row.
        for(int i=0;i<rows.size();i++)rows.set(i,rows.get(i).replace("\n","\\n"));
        Collections.sort(rows);Files.write(Path.of(args[1]),rows);System.out.println("Receiver-local edits: "+rows.size());
    }
}
