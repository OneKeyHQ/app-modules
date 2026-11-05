import java.nio.file.*;
import java.util.*;
import org.jetbrains.java.decompiler.api.plugin.Plugin;
import org.jetbrains.java.decompiler.api.java.*;
import org.jetbrains.java.decompiler.api.plugin.pass.*;
import org.jetbrains.java.decompiler.main.DecompilerContext;
import org.jetbrains.java.decompiler.main.ClassesProcessor.ClassNode;
import org.jetbrains.java.decompiler.modules.decompiler.exps.*;
import org.jetbrains.java.decompiler.modules.decompiler.stats.Statement;

/** Read-only decompiler pass exporting capture identities and final variable names for recovery. */
public final class CaptureMappingPlugin implements Plugin {
    public String id() { return "intercom-capture-mapping"; }
    public String description() { return "Export VarExprent/VarProcessor capture identities; never transform SDK code"; }
    public void registerJavaPasses(JavaPassRegistrar registrar) {
        for (JavaPassLocation location : List.of(JavaPassLocation.AT_END,JavaPassLocation.LATE_PROCESSING)) {
            registrar.register(location,NamedPass.of("ExportCaptures"+location,context->{
                Set<Exprent> visited=Collections.newSetFromMap(new IdentityHashMap<>());
                visit(context.getRoot(),context,location.toString(),visited);
                return false;
            }));
        }
    }
    private static void visit(Statement statement,PassContext context,String phase,Set<Exprent> visited) {
        List<Exprent> roots=new ArrayList<>(statement.getVarDefinitions());
        if (statement.getExprents()!=null) roots.addAll(statement.getExprents());
        roots.addAll(statement.getStatExprents());
        for (Exprent root : roots) {
            List<Exprent> expressions=new ArrayList<>(root.getAllExprents(true));expressions.add(root);
            for (Exprent expression : expressions) if (visited.add(expression)) {
                if (expression instanceof NewExprent created && created.isLambda()) export(created,context,phase);
                if (expression instanceof AssignmentExprent assignment) exportAssignment(assignment,context,phase);
            }
        }
        for (Statement child : statement.getStats()) visit(child,context,phase,visited);
    }
    private static void export(NewExprent created,PassContext context,String phase) {
        ClassNode node=DecompilerContext.getClassProcessor().getMapRootClasses().get(created.getNewType().value);
        if (node==null || node.lambdaInformation==null || created.getConstructor()==null) return;
        List<String> columns=new ArrayList<>(List.of(phase,context.getEnclosingClass().qualifiedName,context.getMethod().getName(),context.getMethod().getDescriptor(),node.lambdaInformation.content_class_name,node.lambdaInformation.content_method_name,node.lambdaInformation.content_method_descriptor));
        BitSet offsets=new BitSet();created.getBytecodeRange(offsets);columns.add(offsets.toString());
        int captureIndex=0;
        for (Exprent parameter : created.getConstructor().getLstParameters()) {
            if (parameter instanceof VarExprent variable) {
                Integer original=variable.getProcessor()==null?null:variable.getProcessor().getVarOriginalIndex(variable.getIndex());
                columns.add("LOCAL|"+original+"|"+variable.getIndex()+"|"+variable.getVersion()+"|"+variable.getName()+"|"+variable.getExprType());
            } else columns.add("UNRESTORED_EXPRESSION|"+parameter.getClass().getSimpleName()+"|"+parameter.getExprType());
            String fieldsTarget=System.getProperty("intercom.capture.fields");
            if(fieldsTarget!=null&&parameter instanceof FieldExprent field){
                List<String> fieldColumns=new ArrayList<>(columns.subList(0,8));fieldColumns.add(""+captureIndex);fieldColumns.add(field.getClassname());fieldColumns.add(field.getName());fieldColumns.add(field.getDescriptor().descriptorString);fieldColumns.add(""+field.isStatic());
                if(field.getInstance() instanceof VarExprent receiver){Integer original=receiver.getProcessor()==null?null:receiver.getProcessor().getVarOriginalIndex(receiver.getIndex());fieldColumns.add("LOCAL|"+original+"|"+receiver.getIndex()+"|"+receiver.getVersion()+"|"+receiver.getName()+"|"+receiver.getExprType());}
                else fieldColumns.add("UNRESTORED_RECEIVER|"+(field.getInstance()==null?"null":field.getInstance().getClass().getSimpleName()));
                append(fieldsTarget,String.join("\t",fieldColumns));
            }
            captureIndex++;
        }
        write(String.join("\t",columns));
    }
    private static void exportAssignment(AssignmentExprent assignment,PassContext context,String phase) {
        String target=System.getProperty("intercom.assignment.map");if(target==null)return;
        if (!(assignment.getLeft() instanceof VarExprent left) || !(assignment.getRight() instanceof VarExprent right))return;
        BitSet offsets=new BitSet();assignment.getBytecodeRange(offsets);
        BitSet rightOffsets=new BitSet();right.getBytecodeRange(rightOffsets);
        String row=String.join("\t",phase,context.getEnclosingClass().qualifiedName,context.getMethod().getName(),context.getMethod().getDescriptor(),left.getName(),left.getExprType().toString(),right.getName(),right.getExprType().toString(),offsets.toString(),rightOffsets.toString());
        append(target,row);
    }
    private static synchronized void append(String target,String row) {
        try { Files.writeString(Path.of(target),row+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND); }
        catch (Exception error) { throw new IllegalStateException(error); }
    }
    private static synchronized void write(String row) {
        String target=System.getProperty("intercom.capture.map");if(target==null)throw new IllegalStateException("Missing capture map output");
        try { Files.writeString(Path.of(target),row+"\n",StandardOpenOption.CREATE,StandardOpenOption.APPEND); }
        catch (Exception error) { throw new IllegalStateException(error); }
    }
}
