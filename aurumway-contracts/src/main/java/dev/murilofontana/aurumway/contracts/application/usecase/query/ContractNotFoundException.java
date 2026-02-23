package dev.murilofontana.aurumway.contracts.application.usecase.query;

public class ContractNotFoundException extends RuntimeException {

    public ContractNotFoundException(String contractId) {
        super("Contract not found: " + contractId);
    }
}
