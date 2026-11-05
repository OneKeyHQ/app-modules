import com.sun.source.tree.*;
import com.sun.source.util.*;
import javax.tools.*;
import java.nio.file.*;
import java.util.*;

/** Move only proven load-only GalleryImage constructor checks into a first-argument source helper. */
public final class GalleryConstructorChecks {
 public static void main(String[] args)throws Exception {
  JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();List<String> rows=new ArrayList<>();
  try(StandardJavaFileManager manager=compiler.getStandardFileManager(null,null,null)) {
   JavacTask task=(JavacTask)compiler.getTask(null,manager,null,List.of("-proc:none"),null,manager.getJavaFileObjects(args[0]));
   for(CompilationUnitTree unit:task.parse()) {
    SourcePositions positions=Trees.instance(task).getSourcePositions();
    new TreePathScanner<Void,Void>() {
     @Override public Void visitMethod(MethodTree method,Void unused) {
      if(!method.getName().contentEquals("<init>")||method.getBody()==null)return null;
      List<? extends StatementTree> statements=method.getBody().getStatements();
      if(statements.size()<2||!(statements.get(statements.size()-1)instanceof ExpressionStatementTree last)||!(last.getExpression()instanceof MethodInvocationTree call)||!call.getMethodSelect().toString().equals("this"))return null;
      int count=statements.size()-1;if(count>5)throw new IllegalStateException("Unexpected GalleryImage check count");
      String[] names={"fileName","mimeType","uri","previewPath","attribution"};
      for(int i=0;i<count;i++) {
       if(!(statements.get(i)instanceof ExpressionStatementTree expression)||!(expression.getExpression()instanceof MethodInvocationTree check)||!check.getMethodSelect().toString().equals("Intrinsics.checkNotNullParameter")||check.getArguments().size()!=2||!check.getArguments().get(0).toString().equals("var"+(i+1))||!(check.getArguments().get(1)instanceof LiteralTree name)||!names[i].equals(name.getValue()))throw new IllegalStateException("Unproven GalleryImage prefix");
      }
      for(ExpressionTree argument:call.getArguments())if(!(argument instanceof IdentifierTree||argument instanceof LiteralTree))throw new IllegalStateException("Constructor argument has computation");
      if(!call.getArguments().get(0).toString().equals("var1"))throw new IllegalStateException("First argument is not original local");
      List<String> helper=new ArrayList<>();for(int i=0;i<5;i++)helper.add(i<count?"var"+(i+1):"null");helper.add(""+count);
      ExpressionTree first=call.getArguments().get(0);
      rows.add(String.join("\t",""+positions.getStartPosition(unit,statements.get(0)),""+positions.getEndPosition(unit,statements.get(count-1)),"","REMOVE_CHECK_PREFIX",""+count));
      rows.add(String.join("\t",""+positions.getStartPosition(unit,first),""+positions.getEndPosition(unit,first),"recoveredCheckedGalleryArguments("+String.join(", ",helper)+")","FIRST_ARGUMENT_CHECK_HELPER",""+count));
      return null;
     }
    }.scan(unit,null);
   }
  }
  Files.write(Path.of(args[1]),rows);System.out.println("Gallery constructor check edits "+rows.size());
 }
}
