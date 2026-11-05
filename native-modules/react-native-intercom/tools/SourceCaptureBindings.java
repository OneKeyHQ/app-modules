import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Check candidate capture names against lexical declarations without type attribution or source mutation. */
public final class SourceCaptureBindings {
    static final Map<String,Map<String,String>> requested=new HashMap<>();
    static final List<String> output=new ArrayList<>();
    static boolean declares(VariableTree variable,String name) { return variable.getName().contentEquals(name); }
    static VariableTree resolve(TreePath path,String name) {
        Tree child=path.getLeaf();
        for (TreePath parent=path.getParentPath();parent!=null;child=parent.getLeaf(),parent=parent.getParentPath()) {
            Tree scope=parent.getLeaf();
            if(scope instanceof BlockTree block) {
                VariableTree found=null;
                for(StatementTree statement:block.getStatements()) {
                    if(statement==child) break;
                    if(statement instanceof VariableTree variable && declares(variable,name)) found=variable;
                }
                if(found!=null)return found;
            } else if(scope instanceof CaseTree branch && branch.getStatements()!=null) {
                VariableTree found=null;
                for(StatementTree statement:branch.getStatements()) {
                    if(statement==child)break;
                    if(statement instanceof VariableTree variable && declares(variable,name))found=variable;
                }
                if(found!=null)return found;
            } else if(scope instanceof MethodTree method) {
                for(VariableTree parameter:method.getParameters())if(declares(parameter,name))return parameter;
            } else if(scope instanceof LambdaExpressionTree lambda) {
                for(VariableTree parameter:lambda.getParameters())if(declares(parameter,name))return parameter;
            } else if(scope instanceof CatchTree catcher && declares(catcher.getParameter(),name)) return catcher.getParameter();
            else if(scope instanceof EnhancedForLoopTree loop && child!=loop.getExpression() && declares(loop.getVariable(),name))return loop.getVariable();
            else if(scope instanceof ForLoopTree loop) {
                for(StatementTree initializer:loop.getInitializer())if(initializer instanceof VariableTree variable && declares(variable,name))return variable;
            } else if(scope instanceof TryTree attempt && child!=attempt.getFinallyBlock() && !attempt.getCatches().contains(child)) {
                for(Tree resource:attempt.getResources())if(resource instanceof VariableTree variable && declares(variable,name))return variable;
            } else if(scope instanceof ClassTree) return null;
        }
        return null;
    }
    public static void main(String[] args)throws Exception {
        for(String line:Files.readAllLines(Path.of(args[0]))) {
            String[] row=line.split("\t");requested.computeIfAbsent(row[0],ignored->new HashMap<>()).put(row[1],row[2]);
        }
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
            List<java.io.File> files=new ArrayList<>();for(String name:requested.keySet())files.add(new java.io.File(name));
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
            JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            for(CompilationUnitTree unit:task.parse()) {
                String file=Path.of(unit.getSourceFile().toUri()).toString();Map<String,String> wanted=requested.get(file);
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitVariable(VariableTree variable,Void unused) {
                        String name=variable.getName().toString();
                        if(wanted.containsKey(name)) {
                            String candidate=wanted.get(name);VariableTree binding=candidate.equals("this")?null:resolve(getCurrentPath(),candidate);
                            long position=Trees.instance(task).getSourcePositions().getStartPosition(unit,variable);
                            output.add(String.join("\t",file,name,candidate,candidate.equals("this")?"THIS":binding==null?"UNRESOLVED":"LOCAL",binding==null?"":binding.toString().replace('\n',' '),""+unit.getLineMap().getLineNumber(position)));
                        }
                        return super.visitVariable(variable,unused);
                    }
                }.scan(unit,null);
            }
            long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();
            Files.write(Path.of(args[1]),output);
            if(errors!=0)throw new IllegalStateException("Source parse errors: "+errors);
        }
        System.out.println("Checked "+output.size()+" lexical capture bindings");
    }
}
