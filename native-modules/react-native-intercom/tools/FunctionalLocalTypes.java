import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Narrow only Object locals whose every assignment already has the same explicit FunctionN cast. */
public final class FunctionalLocalTypes {
    static final class Candidate {String type;boolean unsafe;int assignments;int uses;List<String> evidence=new ArrayList<>();}
    static void assignment(Candidate candidate,ExpressionTree expression,Set<String> imported) {
        candidate.assignments++;
        while(expression instanceof ParenthesizedTree parentheses)expression=parentheses.getExpression();
        candidate.evidence.add(expression.toString().replace('\n',' '));
        if(expression instanceof LiteralTree literal && literal.getValue()==null)return;
        if(!(expression instanceof TypeCastTree cast)){candidate.unsafe=true;return;}
        String type=cast.getType().toString();String simple=type.substring(type.lastIndexOf('.')+1);
        if(!simple.matches("Function[0-9]+") || !(type.equals("kotlin.jvm.functions."+simple) || type.equals(simple)&&imported.contains("kotlin.jvm.functions."+simple))){candidate.unsafe=true;return;}
        String qualified="kotlin.jvm.functions."+simple;
        if(candidate.type!=null && !candidate.type.equals(qualified))candidate.unsafe=true;
        candidate.type=qualified;
    }
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);List<java.io.File> files=new ArrayList<>();
        try(var paths=Files.walk(root)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->files.add(path.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();List<String> rows=new ArrayList<>();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
            JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            for(CompilationUnitTree unit:task.parse()) {
                Map<VariableTree,Candidate> candidates=new IdentityHashMap<>();Set<String> imports=new HashSet<>();for(ImportTree imported:unit.getImports())imports.add(imported.getQualifiedIdentifier().toString());
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitVariable(VariableTree variable,Void unused) {
                        if(variable.getType()!=null && variable.getType().toString().equals("Object") && getCurrentPath().getParentPath().getLeaf() instanceof BlockTree) {
                            Candidate candidate=new Candidate();candidates.put(variable,candidate);
                            if(variable.getInitializer()!=null)assignment(candidate,variable.getInitializer(),imports);
                        }
                        return super.visitVariable(variable,unused);
                    }
                    @Override public Void visitAssignment(AssignmentTree expression,Void unused) {
                        if(expression.getVariable() instanceof IdentifierTree identifier) {
                            Candidate candidate=candidates.get(SourceCaptureBindings.resolve(getCurrentPath(),identifier.getName().toString()));
                            if(candidate!=null)assignment(candidate,expression.getExpression(),imports);
                        }
                        return super.visitAssignment(expression,unused);
                    }
                    @Override public Void visitCompoundAssignment(CompoundAssignmentTree expression,Void unused) {
                        if(expression.getVariable() instanceof IdentifierTree identifier) {
                            Candidate candidate=candidates.get(SourceCaptureBindings.resolve(getCurrentPath(),identifier.getName().toString()));if(candidate!=null)candidate.unsafe=true;
                        }
                        return super.visitCompoundAssignment(expression,unused);
                    }
                }.scan(unit,null);
                // A narrower local can change overload selection. Exclude every uncast
                // invocation/constructor argument or receiver use from this transformation.
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitIdentifier(IdentifierTree identifier,Void unused) {
                        Candidate candidate=candidates.get(SourceCaptureBindings.resolve(getCurrentPath(),identifier.getName().toString()));
                        if(candidate!=null) {
                            Tree child=identifier;TreePath parent=getCurrentPath().getParentPath();
                            while(parent!=null && parent.getLeaf() instanceof ParenthesizedTree){child=parent.getLeaf();parent=parent.getParentPath();}
                            Tree context=parent==null?null:parent.getLeaf();
                            boolean allowed=context instanceof TypeCastTree || context instanceof ReturnTree || context instanceof InstanceOfTree
                                || context instanceof VariableTree variable && variable.getInitializer()==child
                                || context instanceof AssignmentTree assignment && (assignment.getVariable()==child || assignment.getExpression()==child);
                            if(context instanceof BinaryTree binary && (binary.getKind()==Tree.Kind.EQUAL_TO || binary.getKind()==Tree.Kind.NOT_EQUAL_TO)) {
                                ExpressionTree other=binary.getLeftOperand()==child?binary.getRightOperand():binary.getLeftOperand();
                                allowed=other instanceof LiteralTree literal && literal.getValue()==null;
                            }
                            if(!allowed)candidate.unsafe=true;
                            candidate.uses++;
                        }
                        return super.visitIdentifier(identifier,unused);
                    }
                }.scan(unit,null);
                SourcePositions positions=Trees.instance(task).getSourcePositions();String file=Path.of(unit.getSourceFile().toUri()).toString();
                for(var entry:candidates.entrySet()) {
                    Candidate candidate=entry.getValue();VariableTree variable=entry.getKey();if(candidate.unsafe || candidate.type==null || candidate.assignments==0)continue;
                    rows.add(String.join("\t",file,""+positions.getStartPosition(unit,variable.getType()),""+positions.getEndPosition(unit,variable.getType()),candidate.type,variable.getName().toString(),""+candidate.assignments,String.join(" | ",candidate.evidence)));
                }
            }
            long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();if(errors!=0)throw new IllegalStateException("Parse errors: "+errors);
        }
        Collections.sort(rows);Files.write(Path.of(args[1]),rows);System.out.println("Explicitly cast functional locals: "+rows.size());
    }
}
