import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Place only the no-op java.lang.Object constructor before a recovered constructor prefix. */
public final class ObjectConstructorOrder {
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);List<java.io.File> files=new ArrayList<>();
        try(var paths=Files.walk(root)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->files.add(path.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();List<String> rows=new ArrayList<>();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            for(CompilationUnitTree unit:task.parse()) {
                String file=Path.of(unit.getSourceFile().toUri()).toString();SourcePositions positions=Trees.instance(task).getSourcePositions();
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitMethod(MethodTree method,Void unused) {
                        if(!method.getName().contentEquals("<init>") || method.getBody()==null)return super.visitMethod(method,unused);
                        Tree owner=getCurrentPath().getParentPath().getLeaf();if(!(owner instanceof ClassTree clazz) || clazz.getKind()!=Tree.Kind.CLASS)return super.visitMethod(method,unused);
                        if(clazz.getExtendsClause()!=null && !Set.of("Object","java.lang.Object").contains(clazz.getExtendsClause().toString()))return super.visitMethod(method,unused);
                        List<? extends StatementTree> statements=method.getBody().getStatements();
                        for(int i=1;i<statements.size();i++)if(statements.get(i) instanceof ExpressionStatementTree statement && statement.getExpression() instanceof MethodInvocationTree invocation && invocation.getMethodSelect().toString().equals("super") && invocation.getArguments().isEmpty()) {
                            long body=positions.getStartPosition(unit,method.getBody());rows.add(String.join("\t",file,""+(body+1),""+(body+1),"\\n      super();","INSERT_OBJECT_SUPER"));
                            rows.add(String.join("\t",file,""+positions.getStartPosition(unit,statement),""+positions.getEndPosition(unit,statement),"","REMOVE_LATE_OBJECT_SUPER"));
                        }
                        return super.visitMethod(method,unused);
                    }
                }.scan(unit,null);
            }
            long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();if(errors!=0)throw new IllegalStateException("Parse errors: "+errors);
        }
        Collections.sort(rows);Files.write(Path.of(args[1]),rows);System.out.println("Object constructor order edits: "+rows.size());
    }
}
