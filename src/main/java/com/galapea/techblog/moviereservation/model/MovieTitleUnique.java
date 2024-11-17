package com.galapea.techblog.moviereservation.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;
import com.galapea.techblog.moviereservation.service.MovieService;


/**
 * Validate that the title value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = MovieTitleUnique.MovieTitleUniqueValidator.class
)
public @interface MovieTitleUnique {

    String message() default "{Exists.movie.title}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class MovieTitleUniqueValidator implements ConstraintValidator<MovieTitleUnique, String> {

        private final MovieService movieService;
        private final HttpServletRequest request;

        public MovieTitleUniqueValidator(final MovieService movieService,
                final HttpServletRequest request) {
            this.movieService = movieService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(movieService.get(currentId).getTitle())) {
                // value hasn't changed
                return true;
            }
            return !movieService.titleExists(value);
        }

    }

}
