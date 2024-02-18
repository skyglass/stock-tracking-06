package net.greeta.stock.inventory.common.exception;

public class OutOfStockException extends RuntimeException {

    private static final String MESSAGE = "Out of stock";

    public OutOfStockException() {
        super(MESSAGE);
    }

}
