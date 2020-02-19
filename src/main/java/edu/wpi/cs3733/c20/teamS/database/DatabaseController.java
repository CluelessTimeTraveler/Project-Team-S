package edu.wpi.cs3733.c20.teamS.database;

import edu.wpi.cs3733.c20.teamS.ThrowHelper;
import edu.wpi.cs3733.c20.teamS.serviceRequests.JanitorServiceRequest;
import edu.wpi.cs3733.c20.teamS.serviceRequests.RideServiceRequest;
import edu.wpi.cs3733.c20.teamS.serviceRequests.ServiceRequest;
import edu.wpi.cs3733.c20.teamS.serviceRequests.ServiceVisitor;
import org.apache.derby.impl.sql.catalog.SYSROUTINEPERMSRowFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import javax.xml.soap.Node;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class DatabaseController implements DBRepo{
    private static Connection connection = null;

    /**
     * Ensures there is only one connection to the database;
     */
    static {
        createDatabase();
        dropExistingTables();

        /////////////////////CREATE TABLES/////////////////////////////
        try {
            Statement stm = connection.createStatement();

            createNodesTable(stm);
            createEdgeTable(stm);
            createEmployeeTable(stm);
            createServiceableTable(stm);
            createServicesTable(stm);
        }
        catch (SQLException e){
            System.out.print("Failed to create one of tables, aborting...");
            throw new RuntimeException();
        }
    }
    public DatabaseController() {}

    private static void createDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        }catch(ClassNotFoundException ex){
            System.out.println(ex.getMessage());
            System.out.println("Failed to get embedded driver");
            throw new RuntimeException();
        }
        try{
            connection = DriverManager.getConnection("jdbc:derby:hospitalDB;create=true");
            Statement init = connection.createStatement();
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            System.out.println("Failed to connect to database");
            throw new RuntimeException();
        }
    }
    private static void dropExistingTables() {
        try{
            Statement stm = connection.createStatement();
            stm.execute("Drop table EDGES");
        }catch(SQLException e){
            System.out.println("EDGES WAS NOT DROPPED");
        }

        System.out.println("Dropped Table Edges");


        try{
            Statement stm = connection.createStatement();
            stm.execute("Drop table SERVICES");
        }catch(SQLException e){
            System.out.println("SERVICES WAS NOT DROPPED");
        }

        System.out.println("Dropped Table SERVICES");

        try{
            Statement stm = connection.createStatement();
            stm.execute("Drop table NODES");
        }catch(SQLException e){
            System.out.println("NODES WAS NOT DROPPED");
        }

        System.out.println("Dropped Table Nodes");


        try{
            Statement stm = connection.createStatement();
            stm.execute("Drop table SERVICEABLE");
        }catch(SQLException e){
            System.out.println("SERVICEABLE WAS NOT DROPPED");
        }

        System.out.println("Dropped Table SERVICEABLE");

        try{
            Statement stm = connection.createStatement();
            stm.execute("Drop table EMPLOYEES");
        }catch(SQLException e){
            System.out.println("EMPLOYEES WAS NOT DROPPED");
        }

        System.out.println("Dropped Table EMPLOYEES");
    }
    private static void createServicesTable(Statement stm) throws SQLException {
        stm.execute("CREATE TABLE SERVICES(" +
                         "serviceID INTEGER GENERATED BY DEFAULT AS IDENTITY constraint pKey_serviceID primary key," +
                         "serviceType varchar(4)," +
                         "status varchar(50)," +
                         "message varchar(2056)," +
                         "data varchar(9001)," +
                         "assignedEmployee INTEGER CONSTRAINT fKey_empAssigned references EMPLOYEES (employeeID)," +
                         "timeCreated DATE," +
                         "location varchar(1024))");
        System.out.println("Created Table SERVICES");
    }
    private static void createServiceableTable(Statement stm) throws SQLException {
        stm.execute("CREATE TABLE SERVICEABLE(" +
                         "employeeID INTEGER CONSTRAINT fKey_service references EMPLOYEES(employeeID)," +
                         "serviceType varchar(4)," +
                         "constraint pKey_canDO primary key (employeeID, serviceType))");
        System.out.println("Created Table SERVICEABLE");
    }
    private static void createEmployeeTable(Statement stm) throws SQLException {
        stm.execute("CREATE TABLE EMPLOYEES(" +
                        "employeeID INTEGER GENERATED BY DEFAULT AS IDENTITY CONSTRAINT pKey_empID primary key," +
                        "userName varchar(50) constraint uKey_userName unique," +
                        "password varchar(25)," +
                        "accessLevel INTEGER," +
                        "firstName varchar(30)," +
                        "lastName varchar(30))");
        System.out.println("Created Table Employees");
    }
    private static void createEdgeTable(Statement stm) throws SQLException {
        stm.execute("CREATE TABLE EDGES(" +
                        "edgeID varchar(1024) constraint pKey_edgeID PRIMARY KEY," +
                        "startNode varchar(1024) constraint fKey_startNodeID references NODES (nodeID)," +
                        "endNode varchar(1024) constraint fkey_endNodeID references NODES (nodeID))");
        System.out.println("Created Table Edges");
    }
    private static void createNodesTable(Statement stm) throws SQLException {
        stm.execute("CREATE TABLE NODES(" +
                        "nodeID VARCHAR(1024)," +
                        "xcoord double," +
                        "ycoord double," +
                        "floor int," +
                        "building varchar(50)," +
                        "nodeType varchar(4)," +
                        "longName varchar(1024)," +
                        "shortName varchar(50)," +
                        "constraint pkey_nodeID Primary Key (nodeID))");
        System.out.println("Created Table Nodes");
    }

    //Tested
    public void addNode(NodeData nd){
        String insert = "INSERT INTO NODES VALUES(?,?,?,?,?,?,?,?)";
        try{
            PreparedStatement stm = connection.prepareStatement(insert);
            stm.setString(1,nd.getNodeID());
            stm.setDouble(2,nd.getxCoordinate());
            stm.setDouble(3,nd.getyCoordinate());
            stm.setInt(4, nd.getFloor());
            stm.setString(5,nd.getBuilding());
            stm.setString(6,nd.getNodeType());
            stm.setString(7,nd.getLongName());
            stm.setString(8,nd.getShortName());
            stm.execute();
        }
        catch (SQLException e) {
            System.out.println("Failed to insert into nodes");
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }
    //Tested
    public void addSetOfNodes(Set<NodeData> set){
        for(NodeData nd : set){
            addNode(nd);
        }
    }
    //Tested
    public Set<NodeData> getAllNodes(){
        Statement stm = null;
        try{
            stm = connection.createStatement();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        String allNodeString = "SELECT * FROM Nodes";
        ResultSet rset = null;
        Set<NodeData> nodeSet = new HashSet<>();
        try {
            rset = stm.executeQuery(allNodeString);
        }catch(java.sql.SQLException state){
            System.out.println(state.getMessage());
            state.printStackTrace();
            throw new RuntimeException(state);
        }
        nodeSet = parseNodeResultSet(rset);
        try{
            rset.close();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return nodeSet;
    }

    public Set<NodeData> getAllNodesOfType(String type){
        PreparedStatement stm = null;
        String allNodeString = "SELECT * FROM Nodes WHERE NODETYPE = ?";
        System.out.println("Getting nodes of type: " + type);
        try{
            stm = connection.prepareStatement(allNodeString);
            stm.setString(1,type);
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        ResultSet rset = null;
        Set<NodeData> nodeSet = new HashSet<>();
        try {
            rset = stm.executeQuery();
        }catch(java.sql.SQLException state){
            System.out.println(state.getMessage());
            state.printStackTrace();
            throw new RuntimeException(state);
        }
        nodeSet = parseNodeResultSet(rset);
        try{
            rset.close();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return nodeSet;
    }


    //Tested
    private Set<NodeData> parseNodeResultSet(ResultSet rset) {
        Set<NodeData> nodeSet = new HashSet<NodeData>();
        String nodeID;
        double x, y;
        int floor;
        String building;
        String nodeType;
        String longName;
        String shortName;
        try {
            while (rset.next()) {
                nodeID = rset.getString("nodeID");
                x = rset.getDouble("xcoord");
                y = rset.getDouble("ycoord");
                floor = rset.getInt("floor");
                building = rset.getString("building");
                nodeType = rset.getString("nodeType");
                longName = rset.getString("longName");
                shortName = rset.getString("shortName");
                nodeSet.add(new NodeData(nodeID, x, y, floor, building, nodeType, longName, shortName));
            }

        } catch (java.sql.SQLException rsetFailure) {
            System.out.println(rsetFailure.getMessage());
            rsetFailure.printStackTrace();
            throw new RuntimeException(rsetFailure);
        }

        return nodeSet;
    }
    //Tested
    public Set<EdgeData> getAllEdges(){
        Statement stm = null;
        try{
            stm = connection.createStatement();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
        }
        String allNodeString = "SELECT * FROM Edges";
        ResultSet rset = null;
        try {
            rset = stm.executeQuery(allNodeString);
        }catch(java.sql.SQLException state){
            System.out.println(state.getMessage());
            state.printStackTrace();
        }
        Set<EdgeData> edgeSet = parseEdgeResultSet(rset);
        try{
            rset.close();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
        return edgeSet;
    }
    //Tested
    private Set<EdgeData> parseEdgeResultSet(ResultSet rset){
        String edgeID;
        String startNodeID;
        String endNodeID;
        Set<EdgeData> edgeSet = new HashSet<>();
        try{
            while(rset.next()){
                edgeID = rset.getString("edgeID");
                startNodeID = rset.getString("startNode");
                endNodeID = rset.getString("endNode");
                edgeSet.add(new EdgeData(edgeID, startNodeID, endNodeID));
            }
        }catch(java.sql.SQLException rsetFailure){
            System.out.println(rsetFailure.getMessage());
            rsetFailure.printStackTrace();
            throw new RuntimeException();
        }
        return edgeSet;
    }
    //Tested
    public void addEdge(EdgeData edge) {
        String addEntryStr = "INSERT INTO EDGES VALUES (?, ?, ?)";
        try {
            PreparedStatement addStm = connection.prepareCall(addEntryStr);
            addStm.setString(1,edge.getEdgeID());
            addStm.setString(2,edge.getStartNode());
            addStm.setString(3,edge.getEndNode());
            addStm.execute();
            addStm.close();
        }catch(SQLException e){
            throw new RuntimeException();
        }
    }
    //Tested
    public NodeData getNode(String ID){
        String getNodeStr = "SELECT * FROM NODES WHERE NODEID = ?";
        ResultSet rset = null;
        try {
            PreparedStatement getStm = connection.prepareCall(getNodeStr);
            getStm.setString(1,ID);
            rset = getStm.executeQuery();
        }catch(SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
        Set<NodeData> nodeSet = parseNodeResultSet(rset);
        if(nodeSet.isEmpty()){
            System.out.println("Node Not Found");
            return null;
        }
        System.out.println("Got Node");
        NodeData returnNode = null;
        for(NodeData nd : nodeSet){
            returnNode = nd;

        }
        return returnNode;
    }
    //Tested
    public void importStartUpData() {

//        String path = getClass().getResource("/data/allnodes.csv").toString();
//        System.out.println("Path: " + path);
//        path = path.substring(5);
//        System.out.println("Getting Nodes from: " + path);
//        importData("NODES", path, true);

        importNodes();

//        URL resource = getClass().getResource("/data/allnodes.csv");
//        try{
//            System.out.println("URL PATH: " + resource.toURI().toString());
//        }catch(URISyntaxException e){
//            System.out.println(e.getMessage());
//        }
//
//        String path = resource.getPath();
//        path = path.substring(5);
//        //Path path = Paths.get(URI.create(resource.toString()));
//        System.out.println("PATHS GET: " + path);
//        importData("NODES", path, true);



//        String edgePath = getClass().getResource("/data/allEdges.csv").toString();
//        edgePath = edgePath.substring(5);
//        System.out.println("Getting Edges from: " + edgePath);
//        importData("EDGES", edgePath, true);

        importEdges();

//        String empPath = getClass().getResource("/data/employees.csv").toString();
//        empPath = empPath.substring(5);
//        System.out.println("Getting Employees from: " + empPath);
//        importData("EMPLOYEES", empPath, true);

        importEmployees();

        System.out.println("Successfully imported Startup Data (Probably?)");
    }
    //tested, unable to export then import from exprted because of headers
    public int exportData(String toTable, String filePath){
        try {
            //Prepares statement with call
            CallableStatement importStatement = connection.prepareCall("{call SYSCS_UTIL.SYSCS_EXPORT_TABLE(?,?,?,?,?,?)}");
            importStatement.setNull(1,Types.VARCHAR);
            importStatement.setString(2,toTable);
            importStatement.setString(3,filePath);
            importStatement.setNull(4,Types.VARCHAR);
            importStatement.setNull(5,Types.VARCHAR);
            importStatement.setNull(6,Types.VARCHAR);
            importStatement.execute();
        }catch(java.sql.SQLException iS){
            System.out.println("Error exporting...");
            System.out.println(iS.getErrorCode());
            System.out.println(iS.getMessage());
            throw new RuntimeException();
        }
        return 0;
    }

    //Tested
    private int importData(String toTable, String filePath, boolean withHeader){



        //File newFile = new File("resources/data/allnodes.csv");
        //System.out.println(newFile.getAbsolutePath());

        try {
            //Prepares statement with call
            CallableStatement importStatement;
            importStatement = connection.prepareCall("{call SYSCS_UTIL.SYSCS_IMPORT_TABLE_BULK(?,?,?,?,?,?,?,?)}");
            importStatement.setNull(1, Types.VARCHAR);
            importStatement.setString(2,toTable);
            importStatement.setString(3,filePath);
            importStatement.setNull(4,Types.VARCHAR);
            importStatement.setNull(5,Types.VARCHAR);
            importStatement.setNull(6,Types.VARCHAR);
            importStatement.setInt(7,0);
            if(withHeader){
                importStatement.setInt(8,1);
            }else{
                importStatement.setInt(8,0);
            }
            importStatement.execute();
            importStatement.close();
        }catch(java.sql.SQLException iS){
            System.out.println("Error importing...");
            System.out.println(iS.getMessage());
            throw new RuntimeException();
        }
        return 0;
    }
    //Tested
    public void purgeTable(String tableName) {
        String delAllEntryStr = "TRUNCATE TABLE " + tableName;
        try {

            PreparedStatement delStm = connection.prepareCall(delAllEntryStr);
            //delStm.setString(1, tableName);
            delStm.executeUpdate();
            delStm.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //Tested
    public void commit(){
        try{
            connection.commit();
        }catch(SQLException e){
            throw new RuntimeException();
        }

    }
    //Tested
    public void rollBack(){
        try{
            connection.rollback();
        }catch(SQLException e){
            throw new RuntimeException();
        }
    }
    //Tested
    public void autoCommit(boolean isOn){
        try{
            connection.setAutoCommit(isOn);
        }catch(SQLException e){
            throw new RuntimeException();
        }
    }

    Set<ServiceData> getAllServiceRequestData(){
        Statement stm = null;
        try{
            stm = connection.createStatement();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
        }
        String allString = "SELECT * FROM SERVICES";
        ResultSet rset = null;
        try {
            rset = stm.executeQuery(allString);
        }catch(java.sql.SQLException state){
            System.out.println(state.getMessage());
            state.printStackTrace();
        }
        Set<ServiceData> serviceSet = parseServiceResultSet(rset);
        try{
            rset.close();
        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
        return serviceSet;
    }
    Set<ServiceData> parseServiceResultSet(ResultSet rset){
        Set<ServiceData> serviceSet = new HashSet<>();
        int serviceID;
        String serviceType;
        String status;
        String message;
        String data;
        int assignedEmployee;
        Date dateCreated;
        String locationNode;

        try {
            while (rset.next()) {
                serviceID = rset.getInt("serviceID");
                serviceType = rset.getString("serviceType");
                status = rset.getString("STATUS");
                message = rset.getString("message");
                data =rset.getString("DATA");
                assignedEmployee = rset.getInt("ASSIGNEDEMPLOYEE");
                dateCreated = rset.getDate("timecreated");
                locationNode = rset.getString("Location");

                serviceSet.add(new ServiceData(serviceID,serviceType,status,message,data,assignedEmployee,dateCreated,locationNode));
            }

        } catch (java.sql.SQLException rsetFailure) {
            System.out.println(rsetFailure.getMessage());
            rsetFailure.printStackTrace();
            throw new RuntimeException(rsetFailure);
        }

        return serviceSet;


    }
    //Tested
    //  Package-private. Public method should take a ServiceRequest, and use the
    //  visitor pattern to save the correct concrete service-request type.
    public void addServiceRequestData(ServiceData sd) {
        String addEntryStr = "INSERT INTO SERVICES (SERVICETYPE, STATUS, MESSAGE, DATA, ASSIGNEDEMPLOYEE, TIMECREATED, LOCATION) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement addStm = connection.prepareCall(addEntryStr);
            Date currentDate = new Date(Instant.now().toEpochMilli());
            addStm.setString(1,sd.getServiceType());
            addStm.setString(2,sd.getStatus());
            addStm.setString(3,sd.getMessage());
            addStm.setString(4,sd.getData());
            addStm.setInt(5,sd.getAssignedEmployeeID());
            addStm.setDate(6,currentDate);
            addStm.setString(7,sd.getServiceNode());
            addStm.execute();
            addStm.close();
        }catch(SQLException e){
            System.out.println("Failed to add service Request");
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }
    public void updateServiceData(ServiceData sd){
        String updateStr = "UPDATE SERVICES SET STATUS = ?, MESSAGE = ?, DATA = ?, ASSIGNEDEMPLOYEE = ?, LOCATION = ? WHERE SERVICEID = ?";
        PreparedStatement stm = null;
        try{
            stm = connection.prepareStatement(updateStr);
            stm.setString(1,sd.getStatus());
            stm.setString(2,sd.getMessage());
            stm.setString(3,sd.getData());
            stm.setInt(4,sd.getAssignedEmployeeID());
            stm.setString(5,sd.getServiceNode());
            stm.setInt(6,sd.getServiceID());
            stm.executeUpdate();

        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }
    public void deleteServiceWithId(int id){
        String delStr = "DELETE FROM SERVICES WHERE SERVICEID = ?";
        PreparedStatement stm = null;
        try{
            stm = connection.prepareStatement(delStr);
            stm.setInt(1,id);
            stm.executeUpdate();

        }catch(java.sql.SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }


    public void removeNode(String nodeID) {
        String delNode = "DELETE FROM NODES WHERE NODEID = '" + nodeID + "'";
        PreparedStatement stm = null;
        try{
            stm = connection.prepareStatement(delNode);
            stm.executeUpdate();
        }catch(java.sql.SQLException aS){
            System.out.println("Error deleting node...");
            System.out.println(aS.getErrorCode());
            throw new RuntimeException(aS);
        }
    }

    public void removeEdge(String edgeID) {
        String delEdge = "DELETE FROM EDGES WHERE EDGEID = '" + edgeID + "'";
        PreparedStatement stm = null;
        try{
            stm = connection.prepareStatement(delEdge);
            stm.executeUpdate();
        }catch(java.sql.SQLException aS){
            System.out.println("Error deleting edge...");
            System.out.println(aS.getErrorCode());
            throw new RuntimeException(aS);
        }
    }


    //Tested
    public void addEmployee(EmployeeData ed){
        String addEntryStr = "INSERT INTO EMPLOYEES (USERNAME, PASSWORD, ACCESSLEVEL, FIRSTNAME, LASTNAME) VALUES (?,?,?,?,?)";
        try {
            PreparedStatement addStm = connection.prepareCall(addEntryStr);
            addStm.setString(1,ed.getUsername());
            addStm.setString(2,ed.getPassword());
            addStm.setInt(3,ed.getAccessLevel());
            addStm.setString(4,ed.getFirstName());
            addStm.setString(5,ed.getLastName());
            addStm.execute();
            addStm.close();
        }catch(SQLException e){
            System.out.println("Failed to add employee");
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    //Tested
    public boolean checkLogin(String username, String password){
        String checkStr = "SELECT PASSWORD FROM EMPLOYEES WHERE USERNAME = ?";
        try{
            PreparedStatement checkStm = connection.prepareCall(checkStr);
            checkStm.setString(1,username);
            ResultSet rset = checkStm.executeQuery();
            if(rset.next()){
                String passwordOfDatabaseEmployee = rset.getString("PASSWORD");
                if(passwordOfDatabaseEmployee.equals(password)){
                    System.out.println("Password provided: " + password + " Password Found for Given Username: " + passwordOfDatabaseEmployee);
                    System.out.println("Correct password for given username");
                    return true;
                }
                else{
                    System.out.println("Password provided: " + password + " Password Found for Given Username: " + passwordOfDatabaseEmployee);
                    System.out.println("Incorrect password for given username");
                    return false;
                }
            }else{
                System.out.println("Username not found");
                return false;
            }

        }catch(SQLException e){
            throw new RuntimeException();
        }
    }

    public EmployeeData getEmployee(String username){
        String getEmployeeStr = "SELECT * FROM EMPLOYEES WHERE USERNAME = ?";
        try{
            PreparedStatement getStm = connection.prepareCall(getEmployeeStr);
            getStm.setString(1,username);
            ResultSet rset = getStm.executeQuery();
            if(rset.next()){
                int employeeID = rset.getInt("EMPLOYEEID");
                String usernameDB = rset.getString("USERNAME");
                String password = rset.getString("PASSWORD");
                int accessLevel = rset.getInt("ACCESSLEVEL");
                String firstName = rset.getString("FIRSTNAME");
                String lastName = rset.getString("LASTNAME");


                return new EmployeeData(employeeID,usernameDB,password,accessLevel,firstName,lastName);
            }else{
                System.out.println("Username not found");
                return null;
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }


    public void importNodes(){
        try{
            System.out.println("Importing Nodes...");
            InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("/data/allnodes.csv"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            if(br.ready()){
                line = br.readLine();
                line = br.readLine();
                while(line != null){
                    String[] lineArray = line.split(",",-1);
                    NodeData node = new NodeData(lineArray[0],Double.parseDouble(lineArray[1]),Double.parseDouble(lineArray[2]),Integer.parseInt(lineArray[3]),lineArray[4],lineArray[5],lineArray[6],lineArray[7]);
                    System.out.println(node.toString());
                    addNode(node);
                    line = br.readLine();
                }
            }
        }catch(FileNotFoundException f){
            System.out.println(f.getMessage());
            System.out.println(f.getMessage());
        }catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    public void importEdges(){
        try{
            System.out.println("Importing Edges...");
            InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("/data/allEdges.csv"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            if(br.ready()){
                line = br.readLine();
                line = br.readLine();
                while(line != null){
                    String[] lineArray = line.split(",",-1);
                    EdgeData edge = new EdgeData(lineArray[0],lineArray[1],lineArray[2]);
                    System.out.println(edge.toString());
                    addEdge(edge);
                    line = br.readLine();
                }
            }
        }catch(FileNotFoundException f){
            System.out.println(f.getMessage());
            System.out.println(f.getMessage());
        }catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void importEmployees(){
        try{
            System.out.println("Importing Employees...");
            InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("/data/employees.csv"));
            BufferedReader br = new BufferedReader(isr);
            String line;
            if(br.ready()){
                line = br.readLine();
                line = br.readLine();
                while(line != null){
                    String[] lineArray = line.split(",",-1);
                    EmployeeData emp = new EmployeeData(lineArray[1],lineArray[2],Integer.parseInt(lineArray[3]),lineArray[4],lineArray[5]);
                    System.out.println(emp.toString());
                    addEmployee(emp);
                    line = br.readLine();
                }
            }
        }catch(FileNotFoundException f){
            System.out.println(f.getMessage());
            System.out.println(f.getMessage());
        }catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }







}
