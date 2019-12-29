call(acctID, roleName, cl){
    String newName = acctID + "/" + roleName
    cl()
}
