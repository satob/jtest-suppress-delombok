package jp.gr.java_conf.satob.jtestsuppressdelombok;

import static org.openrewrite.java.Assertions.*;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

public class JtestSuppressDelombokRecipeTest implements RewriteTest {
    public void defaults(RecipeSpec spec) {
        spec.recipe(new JtestSuppressDelombokRecipe())
        .parser(JavaParser.fromJavaVersion().classpath("lombok"));
        // spec.recipe(new JtestSuppressDelombokRecipe());
    }

    @Test
    void addsCommentOnAnnotatedMethodWithLombokGenerated() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        rewriteRun(
            java(
                """
                package com.yourorg;

                class FooBar {
                    @lombok.Generated
                    public String hello() {
                        return "Hello from com.yourorg.FooBar!";
                    }
                }
                """,
                """
                package com.yourorg;

                class FooBar {
                    // parasoft-begin-suppress ALL
                    @lombok.Generated
                    public String hello() {
                        return "Hello from com.yourorg.FooBar!";
                    }
                    // parasoft-end-suppress ALL
                }
                """
                )
            );
    }

    @Test
    void addsCommentOnAnnotatedMethodWithGenerated() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        rewriteRun(
            java(
                """
                package com.yourorg;
                import lombok.Generated;

                class FooBar {
                    @Generated
                    public String hello() {
                        return "Hello from com.yourorg.FooBar!";
                    }
                }
                """,
                """
                package com.yourorg;
                import lombok.Generated;

                class FooBar {
                    // parasoft-begin-suppress ALL
                    @Generated
                    public String hello() {
                        return "Hello from com.yourorg.FooBar!";
                    }
                    // parasoft-end-suppress ALL
                }
                """
                )
            );
    }

    @Test
    void addsCommentOnAnnotatedMethodBesideWithExistingComment() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        rewriteRun(
            java(
                """
                package com.yourorg;
                import lombok.Generated;

                class FooBar {
                    // There is a existing comment
                    @Generated
                    public String hello() {
                        return "Hello from com.yourorg.FooBar!";
                        // There is a existing comment
                    }
                    // There is a existing comment
                }
                """,
                """
                package com.yourorg;
                import lombok.Generated;

                class FooBar {
                    // There is a existing comment
                    // parasoft-begin-suppress ALL
                    @Generated
                    public String hello() {
                        return "Hello from com.yourorg.FooBar!";
                        // There is a existing comment
                        // parasoft-end-suppress ALL
                    }
                    // There is a existing comment
                }
                """
                )
            );
    }


    @Test
    void doesNotChangeNotAnnotatedMethod() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        rewriteRun(
            java(
                """
                package com.yourorg;

                class FooBar {
                    @Deprecated
                    public String hello() { return ""; }
                }
                """
            )
        );
    }

    @Test
    void doesNotChangeMethodAnnotatedWithJavaxGenerated() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        rewriteRun(
            spec -> spec.recipe(new JtestSuppressDelombokRecipe()).parser(JavaParser.fromJavaVersion().classpath("javax.annotation-api")),
            java(
                """
                package com.yourorg;
                import javax.annotation.Generated;

                class FooBar {
                    @Generated
                    public String hello() {
                        return "";
                    }
                }
                """
            )
        );
    }

    @Test
    void doesNotChangeAlreadySuppressedMethod() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        rewriteRun(
            java(
                """
                package com.yourorg;
                import lombok.Generated;

                class FooBar {
                    // parasoft-begin-suppress ALL
                    @Generated
                    public String hello() { return ""; }
                    // parasoft-end-suppress ALL
                }
                """
            )
        );
    }
}
