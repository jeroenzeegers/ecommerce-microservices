package com.hoangtien2k3.promotion.validation;

import com.hoangtien2k3.promotion.model.enumeration.DiscountType;
import com.hoangtien2k3.promotion.model.enumeration.UsageType;
import com.hoangtien2k3.promotion.viewmodel.PromotionDto;
import org.springframework.util.CollectionUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PromotionValidator implements ConstraintValidator<PromotionConstraint, PromotionDto> {

    private boolean isCreate;

    @Override
    public void initialize(PromotionConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(PromotionDto promotionDto, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = true;
        if (UsageType.LIMITED.equals(promotionDto.getUsageType())) {
            isValid = promotionDto.getUsageLimit() > 0;
        }

        isValid = isValid && isValidDiscountType(promotionDto);

        isValid = isValid && isValidApplyToItems(promotionDto, isValid);

        return isValid;
    }

    private static boolean isValidDiscountType(PromotionDto promotion) {
        if (DiscountType.FIXED.equals(promotion.getDiscountType())) {
            return promotion.getDiscountAmount() > 0;
        } else {
            return promotion.getDiscountPercentage() > 0;
        }
    }

    private static boolean isValidApplyToItems(PromotionDto promotion, boolean isValid) {
        return switch (promotion.getApplyTo()) {
            case PRODUCT -> isValid && !CollectionUtils.isEmpty(promotion.getProductIds());
            case BRAND -> isValid && !CollectionUtils.isEmpty(promotion.getBrandIds());
            case CATEGORY -> isValid && !CollectionUtils.isEmpty(promotion.getCategoryIds());
        };
    }
}
