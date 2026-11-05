import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Recover narrowed coroutine locals only when every source assignment uses one concrete continuation type. */
public final class ContinuationLocalTypes {
    static final class Candidate {
        String type;boolean unsafe;int assignments;List<String> evidence=new ArrayList<>();
    }
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);List<java.io.File> files=new ArrayList<>();
        try(var paths=Files.walk(root)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->files.add(path.toFile()));}
        List<String> rows=new ArrayList<>();JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
            JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            for(CompilationUnitTree unit:task.parse()) {
                Map<VariableTree,Candidate> candidates=new IdentityHashMap<>();
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitVariable(VariableTree variable,Void unused) {
                        if(variable.getType()!=null && variable.getType().toString().equals("Continuation") && variable.getInitializer()==null && getCurrentPath().getParentPath().getLeaf() instanceof BlockTree)candidates.put(variable,new Candidate());
                        return super.visitVariable(variable,unused);
                    }
                    @Override public Void visitAssignment(AssignmentTree assignment,Void unused) {
                        if(assignment.getVariable() instanceof IdentifierTree identifier) {
                            VariableTree variable=SourceCaptureBindings.resolve(getCurrentPath(),identifier.getName().toString());Candidate candidate=candidates.get(variable);
                            if(candidate!=null) {
                                ExpressionTree value=assignment.getExpression();String type=value instanceof TypeCastTree cast?cast.getType().toString():value instanceof NewClassTree created?created.getIdentifier().toString():null;
                                if(type==null || candidate.type!=null && !candidate.type.equals(type))candidate.unsafe=true;
                                if(type!=null)candidate.type=type;
                                candidate.assignments++;candidate.evidence.add(assignment.toString().replace('\n',' '));
                            }
                        }
                        return super.visitAssignment(assignment,unused);
                    }
                }.scan(unit,null);
                String file=Path.of(unit.getSourceFile().toUri()).toString();
                for(var entry:candidates.entrySet()) {
                    VariableTree variable=entry.getKey();Candidate candidate=entry.getValue();
                    if(candidate.unsafe || candidate.assignments<2 || candidate.type==null)continue;
                    Path concrete=Path.of(file).getParent().resolve(candidate.type+".java");
                    if(!Files.isRegularFile(concrete))continue;
                    String source=Files.readString(concrete);
                    if(!source.matches("(?s).*\\bextends (?:ContinuationImpl|SuspendLambda)\\b.*"))continue;
                    SourcePositions positions=Trees.instance(task).getSourcePositions();long start=positions.getStartPosition(unit,variable.getType()),end=positions.getEndPosition(unit,variable.getType());
                    rows.add(String.join("\t",file,""+start,""+end,candidate.type,""+candidate.assignments,String.join(" | ",candidate.evidence)));
                }
            }
            long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();
            if(errors!=0)throw new IllegalStateException("Parse errors: "+errors);
        }
        Collections.sort(rows);Files.write(Path.of(args[1]),rows);System.out.println("Concrete continuation locals: "+rows.size());
    }
}
