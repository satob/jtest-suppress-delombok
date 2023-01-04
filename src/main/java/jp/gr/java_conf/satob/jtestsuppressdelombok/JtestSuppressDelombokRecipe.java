package jp.gr.java_conf.satob.jtestsuppressdelombok;

import java.util.Arrays;
import java.util.Iterator;

import org.openrewrite.ExecutionContext;
import org.openrewrite.PrintOutputCapture;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.Comment;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.TextComment;
import org.openrewrite.marker.Markers;

public class JtestSuppressDelombokRecipe extends Recipe {

    @Override
    public String getDisplayName() {
        return "Suppress Jtest findings on delombok-generated code";
    }

    @Override
    public String getDescription() {
        return "Add suppress comment for Parasoft Jtest around delombok-generated methods/fields.";
    }

    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new JtestSuppressDelombokVisitor();
    }

    public class JtestSuppressDelombokVisitor extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public MethodDeclaration visitMethodDeclaration(MethodDeclaration methodDecl, ExecutionContext context) {
            /*
            if (methodDecl.getLeadingAnnotations().size() > 0) {
                System.out.println(methodDecl.getLeadingAnnotations().get(0).toString());
                System.out.println(methodDecl.getLeadingAnnotations().get(0).getType().toString());
            }
            //*/
            /*
            boolean hasGeneratedAnnotation = methodDecl.getLeadingAnnotations().stream()
                    .anyMatch(annotation -> annotation.getSimpleName().equals("Generated"));
            if (hasGeneratedAnnotation) {
                System.out.println(hasGeneratedAnnotation);
                System.out.println(methodDecl.getLeadingAnnotations().get(0).toString());
                System.out.println(methodDecl.getLeadingAnnotations().get(0).getType().toString());
            }
            //*/
            boolean hasGeneratedAnnotation = methodDecl.getLeadingAnnotations().stream()
                    .anyMatch(annotation -> annotation.getType().toString().equals("lombok.Generated"));
            // boolean alreadyHasSuppressComment = methodDecl.getPrefix().getComments().stream()
            //         .anyMatch(comment -> comment.toString().matches(".*parasoft-begin-suppress\sALL.*"));

            /*
            boolean alreadyHasSuppressComment = methodDecl.getPrefix().getComments().stream()
                    .anyMatch(new Predicate<Comment>() {
                        Cursor cursor;

                        public Predicate(Cursor cursor) {
                            this.cursor = cursor;
                        }
                        @Override
                        public boolean test(Comment comment) {
                            PrintOutputCapture<String> p = new PrintOutputCapture<String>("");
                            comment.printComment(this.getCursor(), p);
                            return p.out.toString().matches(".*parasoft-begin-suppress\sALL.*");
                        }
                    });
            //*/

            Iterator<Comment> it = methodDecl.getPrefix().getComments().iterator();
            boolean alreadyHasSuppressComment = false;
            while (it.hasNext()) {
                Comment comment = it.next();
                PrintOutputCapture<String> p = new PrintOutputCapture<String>("");
                comment.printComment(this.getCursor(), p);
                if (p.out.toString().matches(".*parasoft-begin-suppress\sALL.*")) {
                    alreadyHasSuppressComment = true;
                    break;
                }
            }

            /*
            if (hasGeneratedAnnotation && methodDecl.getPrefix().getComments().size() > 0) {
                System.out.println(methodDecl.getLeadingAnnotations().get(0).toString());

                PrintOutputCapture<String> p = new PrintOutputCapture<String>("");
                methodDecl.getPrefix().getComments().get(0).printComment(this.getCursor(), p);
                System.out.println(p.out);

                System.out.println(methodDecl.getLeadingAnnotations().get(0).getType().toString());
            }
            //*/


            if (hasGeneratedAnnotation && !alreadyHasSuppressComment) {
                // methodDecl.getPrefix().getComments().add(new TextComment(false, "parasoft-begin-suppress ALL", "\n", Markers.EMPTY));
                // methodDecl.getLeadingAnnotations().get(0).getComments().add(new TextComment(false, "parasoft-begin-suppress ALL", "\n", Markers.EMPTY));
                // if (methodDecl.getLeadingAnnotations().get(0).getPrefix().getComments().size() == 0) {

                if (methodDecl.getPrefix().getComments().size() == 0) {
                    methodDecl = methodDecl.withComments(Arrays.asList(new TextComment(false, "parasoft-begin-suppress ALL", "\n", Markers.EMPTY)));
                } else {
                    methodDecl.getPrefix().getComments().add(new TextComment(false, "parasoft-begin-suppress ALL", "\n", Markers.EMPTY));
                }
                methodDecl.getBody().getEnd().getComments().add(new TextComment(false, "parasoft-end-suppress ALL", "\n", Markers.EMPTY));

                /*
                final JavaTemplate endSuppressCommentTemplate =
                        JavaTemplate.builder(this::getCursor, "// parasoft-end-suppress ALL").build();
                methodDecl = methodDecl.withBody(methodDecl.getBody().withTemplate(
                        endSuppressCommentTemplate,
                        methodDecl.getBody().getCoordinates().lastStatement()
                        ));
                */

                return methodDecl;
            }

            return methodDecl;
        }
    }
}
