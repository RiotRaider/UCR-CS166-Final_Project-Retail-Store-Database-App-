CREATE OR REPLACE LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_prod_order()
RETURNS "trigger" AS
$BODY$
BEGIN
    UPDATE Product SET numberOfUnits = numberOfUnits - NEW.unitsOrdered WHERE storeID = NEW.storeID AND productName = NEW.productName;
    RETURN new;
END
$BODY$
LANGUAGE plpgsql VOLATILE;

DROP TRIGGER IF EXISTS update_order_trigger ON Orders;
CREATE TRIGGER update_order_trigger
AFTER INSERT
ON Orders
FOR EACH ROW
EXECUTE PROCEDURE update_prod_order();