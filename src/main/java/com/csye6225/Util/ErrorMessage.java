package com.csye6225.Util;

public class ErrorMessage {
    public static final String INVALID_UPDATE_OTHER_INFORMATION ="Only first name, last name and password can be updated";
    public static final String REPEAD_EMAIL = "Repeat Email: ";

    public static final String UNAUTHORIZED = "Authentication failed";

    public static final String EXPIRED = "Token expired";

    public static final String INVALID_EMAIL = "Invalid email";

    public static final String CHANGE_OTHER_INFORMATION = "Unauthorized, Cannot change or get other information";

    public static final String GET_OTHER_INFORMATION = "Unauthorized, Cannot get other information";
    // Product related
    public static final String PRODUCT_NOT_EXIST = "Product not exist";

    public static final String PRODUCT_QUANTITY_ERROR = "Product quantity should greater than 0";

    public static final String REPEAT_SKU = "You can't have the create/update product with same sku";

    public static final String NO_CONTENT ="Please enter all the necessary fields";

    public static final String PARSE_ERROR = "Can't parse the input";

    public static final String IMAGE_NOT_FOUND = "Image not found";

}
