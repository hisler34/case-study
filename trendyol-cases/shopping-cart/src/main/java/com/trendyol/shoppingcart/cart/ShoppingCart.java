package com.trendyol.shoppingcart.cart;import com.trendyol.shoppingcart.campaign.CampaignComponent;import com.trendyol.shoppingcart.category.CategoryComponent;import com.trendyol.shoppingcart.coupon.CouponComponent;import com.trendyol.shoppingcart.delivery.Calculator;import com.trendyol.shoppingcart.delivery.DeliveryCostCalculator;import com.trendyol.shoppingcart.discount.withcampaign.DiscountFactory;import com.trendyol.shoppingcart.discount.withcampaign.IDiscountStrategy;import com.trendyol.shoppingcart.discount.withcoupon.ApplyCouponFactory;import com.trendyol.shoppingcart.discount.withcoupon.ICouponStrategy;import com.trendyol.shoppingcart.product.ProductComponent;import com.trendyol.shoppingcart.utilities.Utils;import lombok.Getter;import lombok.Setter;import java.util.*;/** * */@Setter@Getterpublic class ShoppingCart {    private Map<ProductComponent, Integer> products;    private Map<CategoryComponent, Map<ProductComponent, Integer>> groupedProductsByCategory;    private CartResult result;    private Calculator deliveryCostCalculator;    public ShoppingCart() {        //to keep the products in the same order they were inserted        this.products = new LinkedHashMap<>();        this.groupedProductsByCategory = new LinkedHashMap<>();        this.result = new CartResult();    }    /**     * Add Item into Shopping Cart     * @param product     * @param quantity     */    public void addItem(ProductComponent product, int quantity) {        int previousQuantity = this.products.containsKey(product) ? this.products.get(product) : 0;        int currentQuantity = previousQuantity + quantity;        this.products.put(product, currentQuantity);        retrieveGroupedProductsByCategory();        this.result.setTotalAmount(totalAmount());    }    /**     * Remove item from Shopping Cart     * @param product     * @param quantity     */    public void removeItem(ProductComponent product, int quantity) {        int previousQuantity = this.products.containsKey(product) ? this.products.get(product) : 0;        int currentQuantity = previousQuantity - quantity;        //to check whether all products are removed or not        if (currentQuantity == 0) {            this.products.remove(product);        } else if (currentQuantity > 0) {            this.products.replace(product, currentQuantity);        } else {            throw new IllegalArgumentException("There are not " + quantity + " product to remove from Shopping Cart ");        }        retrieveGroupedProductsByCategory();        result.setTotalAmount(totalAmount());    }    /**     * Retrieve products which are grouped by category     */    private void retrieveGroupedProductsByCategory() {        for (Map.Entry<ProductComponent, Integer> entry : products.entrySet()) {            if (!this.groupedProductsByCategory.containsKey(entry.getKey().getCategory())) {                Map<ProductComponent, Integer> map = new LinkedHashMap<>();                map.put(entry.getKey(), entry.getValue());                this.groupedProductsByCategory.put(entry.getKey().getCategory(), map);            } else {                this.groupedProductsByCategory.get(entry.getKey().getCategory()).put(entry.getKey(), entry.getValue());            }        }    }    /**     * Calculate total amount of given quantity of products     * @return totalAmount     */    public double totalAmount() {        double totalPrice = 0;        for (ProductComponent items : this.products.keySet()) {            totalPrice += items.priceForQuantity(this.products.get(items));        }        return totalPrice;    }    /**     * @param discounts     */    public void applyDiscount(List<CampaignComponent> discounts) {        //Keeps a list of discount strategies based on the campaign conditions applied to categories        List<IDiscountStrategy> discountStrategies = DiscountFactory.getDiscountStrategy(discounts);        double totalAmount = totalAmount();        double maxDiscount = 0;        double resultStrategyDiscount;        double totalAmountAfterDiscount;        //Decides on maximum discount by making calculation according to each discount strategy.//todo optimize  O(n^3)        for (IDiscountStrategy strategy : discountStrategies) {            resultStrategyDiscount = strategy.calculateDiscount(this.groupedProductsByCategory);            if (totalAmount > resultStrategyDiscount) {                if (maxDiscount < resultStrategyDiscount)                    maxDiscount = resultStrategyDiscount;            }        }        // Calculates the total amount after the discount and Adds to the shopping card result        totalAmountAfterDiscount = totalAmount - maxDiscount;        this.result.setTotalDiscountWithCampaign(maxDiscount);        this.result.setTotalAmountAfterDiscount(totalAmountAfterDiscount);    }    /**     * Decides on discount by making calculation according to each coupon strategy     * @param coupon     */    public void applyCoupon(CouponComponent coupon) {        ICouponStrategy couponStrategy = ApplyCouponFactory.getApplyCoupon(coupon);        double totalAmount = result.getTotalAmountAfterDiscount() == 0 ? totalAmount() : result.getTotalAmountAfterDiscount();        // Calculates the total amount after the discount and Adds to the shopping card result        double totalAmountAfterDiscount = couponStrategy.calculateCouponDiscount(totalAmount);        this.result.setTotalDiscountWithCoupon(totalAmount - totalAmountAfterDiscount);        this.result.setTotalAmountAfterDiscount(totalAmountAfterDiscount);    }    public void getDeliveryCost() {        //todo Enumeration arguments for getInstance method        this.deliveryCostCalculator = DeliveryCostCalculator.getInstance(3, 3, 2.99);        double deliveryCost = deliveryCostCalculator.calculateFor(this);        this.result.setDeliveryCost(Utils.formatTwoDigitsAfterComma(deliveryCost));    }    public void print() {        this.result.setGroupedProductsByCategory(this.groupedProductsByCategory);        String r = this.result.toString();        System.out.println(r);    }    public boolean isEmpty() {        return getProducts().isEmpty();    }    public int getNumberOfDifferentProduct() {        return this.products.keySet().size();    }    public int getNumberOfDifferentCategory() {        return this.groupedProductsByCategory.keySet().size();    }    public int getNumberOfProduct() {        return this.products.values().stream().mapToInt(i -> i).sum();    }}