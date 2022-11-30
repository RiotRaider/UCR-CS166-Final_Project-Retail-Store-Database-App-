DROP INDEX IF EXISTS Store_Manager_Index;
DROP INDEX IF EXISTS Product_Store_Index;
DROP INDEX IF EXISTS Customer_Order_Index;

CREATE INDEX Store_Manager_Index ON Store(managerID);
CREATE INDEX Product_Store_Index ON Product(storeID);
CREATE INDEX Customer_Order_Index ON Orders(customerID);