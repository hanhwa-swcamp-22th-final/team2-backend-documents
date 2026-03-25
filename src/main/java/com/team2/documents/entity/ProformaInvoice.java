package com.team2.documents.entity;

public class ProformaInvoice {

    private final String piId;
    private ProformaInvoiceStatus status;

    public ProformaInvoice(String piId, ProformaInvoiceStatus status) {
        this.piId = piId;
        this.status = status;
    }

    public String getPiId() {
        return piId;
    }

    public ProformaInvoiceStatus getStatus() {
        return status;
    }

    public void requestRegistration() {
        if (!ProformaInvoiceStatus.DRAFT.equals(status)) {
            throw new IllegalStateException("초안 상태의 PI만 등록 요청할 수 있습니다.");
        }
        this.status = ProformaInvoiceStatus.APPROVAL_PENDING;
    }
}
