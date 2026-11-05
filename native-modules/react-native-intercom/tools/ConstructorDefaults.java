import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Fold ordered independent default-parameter assignments into the Java constructor invocation. */
public final class ConstructorDefaults {
    static boolean references(Tree tree,Set<String> names) {
        boolean[] found={false};new TreeScanner<Void,Void>() {
            @Override public Void visitIdentifier(IdentifierTree identifier,Void unused) {
                if(names.contains(identifier.getName().toString()))found[0]=true;return null;
            }
        }.scan(tree,null);return found[0];
    }
    public static void main(String[] args)throws Exception {
        Path root=Path.of(args[0]);List<java.io.File> files=new ArrayList<>();
        if(args.length>2)for(String file:Files.readAllLines(Path.of(args[2])))files.add(new java.io.File(file));
        else try(var paths=Files.walk(root)){paths.filter(path->path.toString().endsWith(".java")).forEach(path->files.add(path.toFile()));}
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();List<String> output=new ArrayList<>();
        try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
            DiagnosticCollector<JavaFileObject> diagnostics=new DiagnosticCollector<>();
            JavacTask task=(JavacTask)compiler.getTask(null,manager,diagnostics,List.of("-proc:none"),null,manager.getJavaFileObjectsFromFiles(files));
            for(CompilationUnitTree unit:task.parse()) {
                String file=Path.of(unit.getSourceFile().toUri()).toString(),source=Files.readString(Path.of(file));SourcePositions positions=Trees.instance(task).getSourcePositions();
                new TreePathScanner<Void,Void>() {
                    @Override public Void visitMethod(MethodTree method,Void unused) {
                        if(!method.getName().contentEquals("<init>") || method.getBody()==null)return super.visitMethod(method,unused);
                        List<? extends StatementTree> statements=method.getBody().getStatements();
                        if(args.length>2 && statements.size()==1 && statements.get(0) instanceof ExpressionStatementTree only && only.getExpression() instanceof MethodInvocationTree existing && existing.getMethodSelect().toString().equals("this")) {
                            Map<String,String> parameterTypes=new HashMap<>();for(VariableTree parameter:method.getParameters())parameterTypes.put(parameter.getName().toString(),parameter.getType().toString());
                            List<String> arguments=new ArrayList<>();boolean changed=false;
                            for(ExpressionTree argument:existing.getArguments()) {
                                ExpressionTree unwrapped=argument;while(unwrapped instanceof ParenthesizedTree parentheses)unwrapped=parentheses.getExpression();
                                if(unwrapped instanceof ConditionalExpressionTree conditional && conditional.getFalseExpression() instanceof IdentifierTree identifier && parameterTypes.containsKey(identifier.getName().toString())) {
                                    String condition=conditional.getCondition().toString().replaceAll("\\s+","");
                                    if(condition.matches("\\(*var[0-9]+&-?[0-9]+\\)*!=0\\)*")) {
                                        arguments.add("("+conditional.getCondition()+" ? ("+parameterTypes.get(identifier.getName().toString())+") ("+conditional.getTrueExpression()+") : "+identifier+")");changed=true;continue;
                                    }
                                }
                                arguments.add(argument.toString());
                            }
                            if(changed)output.add(String.join("\t",file,""+positions.getStartPosition(unit,only),""+positions.getEndPosition(unit,only),"this("+String.join(", ",arguments)+");","BOXED_ARM_REPAIR"));
                            return super.visitMethod(method,unused);
                        }
                        if(statements.size()<2 || !(statements.get(statements.size()-1) instanceof ExpressionStatementTree expression) || !(expression.getExpression() instanceof MethodInvocationTree call) || !call.getMethodSelect().toString().equals("this"))return super.visitMethod(method,unused);
                        Map<String,ExpressionTree> conditions=new LinkedHashMap<>(),defaults=new LinkedHashMap<>();boolean valid=true;
                        Set<String> parameters=new HashSet<>();Map<String,String> parameterTypes=new HashMap<>();for(VariableTree parameter:method.getParameters()){parameters.add(parameter.getName().toString());parameterTypes.put(parameter.getName().toString(),parameter.getType().toString());}
                        for(StatementTree statement:statements.subList(0,statements.size()-1)) {
                            if(!(statement instanceof IfTree branch) || branch.getElseStatement()!=null){valid=false;break;}
                            StatementTree then=branch.getThenStatement();if(then instanceof BlockTree block && block.getStatements().size()==1)then=block.getStatements().get(0);
                            if(!(then instanceof ExpressionStatementTree assignmentStatement) || !(assignmentStatement.getExpression() instanceof AssignmentTree assignment) || !(assignment.getVariable() instanceof IdentifierTree identifier)){valid=false;break;}
                            String name=identifier.getName().toString();String condition=branch.getCondition().toString().replaceAll("\\s+","");
                            if(!parameters.contains(name) || defaults.containsKey(name) || !condition.matches("\\(*var[0-9]+&-?[0-9]+\\)*!=0\\)*")){valid=false;break;}
                            conditions.put(name,branch.getCondition());defaults.put(name,assignment.getExpression());
                        }
                        if(!valid)return super.visitMethod(method,unused);
                        for(ExpressionTree value:defaults.values())if(references(value,defaults.keySet()))valid=false;
                        List<String> encountered=new ArrayList<>(),arguments=new ArrayList<>();
                        for(ExpressionTree argument:call.getArguments()) {
                            if(!(argument instanceof IdentifierTree identifier)){valid=false;break;}
                            String name=identifier.getName().toString();
                            if(defaults.containsKey(name)) {
                                encountered.add(name);arguments.add("("+conditions.get(name)+" ? ("+parameterTypes.get(name)+") ("+defaults.get(name)+") : "+name+")");
                            } else arguments.add(name);
                        }
                        if(!valid || !encountered.equals(new ArrayList<>(defaults.keySet())))return super.visitMethod(method,unused);
                        long start=positions.getStartPosition(unit,statements.get(0)),end=positions.getEndPosition(unit,expression);
                        output.add(String.join("\t",file,""+start,""+end,"this("+String.join(", ",arguments)+");",String.join(",",encountered)));
                        return super.visitMethod(method,unused);
                    }
                }.scan(unit,null);
            }
            long errors=diagnostics.getDiagnostics().stream().filter(d->d.getKind()==Diagnostic.Kind.ERROR).count();if(errors!=0)throw new IllegalStateException("Parse errors: "+errors);
        }
        Collections.sort(output);Files.write(Path.of(args[1]),output);System.out.println("Ordered independent constructor defaults: "+output.size());
    }
}
