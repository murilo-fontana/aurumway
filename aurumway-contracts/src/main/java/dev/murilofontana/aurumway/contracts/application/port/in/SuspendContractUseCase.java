package dev.murilofontana.aurumway.contracts.application.port.in;

public interface SuspendContractUseCase {

    ContractStatusResult execute(String contractId);

    record ContractStatusResult(String contractId, String status) {}
}
