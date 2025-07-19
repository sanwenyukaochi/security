package com.sanwenyukaochi.security.ao;

import cn.hutool.crypto.CryptoException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sanwenyukaochi.security.security.jwt.RSAUtil;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginAO {
    @NotBlank(message = "请输入用户名")
    @Size(min = 6, max = 20, message = "用户名长度应为6-20字符")
    private String username;
    
    @NotBlank(message = "请输入密码")
    @PasswordSize(min = 6, max = 20, message = "密码长度应为6~20个字符")
    private String password;

    @NotBlank(message = "请输入验证码Key")
    private String captchaKey;

    @NotBlank(message = "请输入验证码")
    private String captcha;

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = DecryptedLengthValidator.class)
    @Documented
    public @interface PasswordSize {
        String message() default "字段长度不符合要求";
        int min() default 0;
        int max() default Integer.MAX_VALUE;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }


    public static class DecryptedLengthValidator implements ConstraintValidator<PasswordSize, String> {

        private int min;
        private int max;

        @Override
        public void initialize(PasswordSize passwordSize) {
            this.min = passwordSize.min();
            this.max = passwordSize.max();
        }

        @Override
        public boolean isValid(String encryptedValue, ConstraintValidatorContext context) {
            if (StringUtils.isBlank(encryptedValue)) {
                return true;
            }

            try {
                int length = RSAUtil.decrypt(encryptedValue).length();
                return length >= min && length <= max;
            } catch (Exception e) {
                throw new CryptoException("RSA 解密失败");
            }
        }
    }
}
