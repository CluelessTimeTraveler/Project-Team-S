package edu.wpi.cs3733.c20.teamS.serviceRequests;

public abstract class ServiceVisitor {
    public abstract void visit(JanitorServiceRequest request);
    public abstract void visit(RideServiceRequest request);
    public abstract void visit(DrugServiceRequest request);


    public abstract void visit(GiftServiceRequest giftServiceRequest);

    public abstract void visit(FloristServiceRequest request);
    public abstract void visit(InterpreterServiceRequest request);
    public abstract void visit(MaintenanceServiceRequest request);
    public abstract void visit(SecurityServiceRequest request);
    public abstract void visit(ServiceTechServiceRequest request);
    public abstract void visit(LaundryServiceRequest request);
    public abstract void visit(LastRitesRequest request);

}
