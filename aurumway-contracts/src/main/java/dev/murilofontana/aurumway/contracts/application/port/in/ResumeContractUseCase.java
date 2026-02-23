package dev.murilofontana.aurumway.contracts.application.port.in;

public interface ResumeContractUseCase {

    ContractStatusResult execute(String contractId);

    record ContractStatusResult(String contractId, String status) {}
}
