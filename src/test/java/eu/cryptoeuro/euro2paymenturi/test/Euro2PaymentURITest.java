package eu.cryptoeuro.euro2paymenturi.test;
import eu.cryptoeuro.euro2paymenturi.Euro2PaymentURI;
import org.junit.Test;

import static org.junit.Assert.*;

public class Euro2PaymentURITest {

    @Test
    public void testParseForAddressMethod() {
    	Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment");
    	assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertNull(euro2PaymentURI.getAmount());
    	assertNull(euro2PaymentURI.getMessage());
    	assertEquals(euro2PaymentURI.getParameters().size(), 0);
    }

    @Test
    public void testParseForAddressWithNameMethod() {
    	Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?payer=222xxxxRRRReeee4Zx6rewF9WQrcZv245W");

    	assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
		assertEquals(euro2PaymentURI.getPayer(), "222xxxxRRRReeee4Zx6rewF9WQrcZv245W");
    	assertNull(euro2PaymentURI.getAmount());
    	assertNull(euro2PaymentURI.getMessage());
    	assertEquals(euro2PaymentURI.getParameters().size(), 0);
    }

    @Test
    public void testParseForAddressWithAmountAndNameMethod() {
    	Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=20.3");

    	assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertEquals(euro2PaymentURI.getAmount(), 20,3);
    	assertNull(euro2PaymentURI.getMessage());
    	assertEquals(euro2PaymentURI.getParameters().size(), 0);
    }

    @Test
	public void testParseForAddressWithAmountAndMessageAndRequiredParameterMethod() {
		Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=50&message=Donation%20for%20project%20xyz");

		assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
		assertEquals(euro2PaymentURI.getAmount(), Double.valueOf(50));
		assertEquals(euro2PaymentURI.getMessage(), "Donation for project xyz");
		assertEquals(euro2PaymentURI.getParameters().size(), 0);
	}

	@Test
	public void testParse() {
		Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=50&message=Donation%20for%20project%20xyz&payer=XXX333PAYERADDRESS&signature=REQUESTCREATORS_SIGNATURE");

		assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
		assertEquals(euro2PaymentURI.getAmount(), Double.valueOf(50));
		assertEquals(euro2PaymentURI.getMessage(), "Donation for project xyz");
		assertEquals(euro2PaymentURI.getPayer(), "XXX333PAYERADDRESS");
		assertEquals(euro2PaymentURI.getSignature(), "REQUESTCREATORS_SIGNATURE");
		assertEquals(euro2PaymentURI.getParameters().size(), 0);
	}

    @Test
    public void testParseForAddressWithParametersMethod() {
		Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?somethingyoudontunderstand=50&somethingelseyoudontget=999&r=https%3A%2F%2Ffoo.com%2Fi%2F7BpFbVsnh5PUisfh&req-app=appname");

    	assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertNull(euro2PaymentURI.getAmount());
    	assertNull(euro2PaymentURI.getMessage());
        assertEquals(euro2PaymentURI.getParameters().size(), 4);
    	assertEquals(euro2PaymentURI.getParameters().get("somethingyoudontunderstand").getValue(), "50");
    	assertEquals(euro2PaymentURI.getParameters().get("somethingelseyoudontget").getValue(), "999");
        assertEquals(euro2PaymentURI.getParameters().get("r").getValue(), "https://foo.com/i/7BpFbVsnh5PUisfh");
    	assertEquals(euro2PaymentURI.getParameters().get("app").getValue(), "appname");
    	assertTrue(euro2PaymentURI.getParameters().get("app").isRequired());
    }

    @Test
    public void testParseForInvalidAddressesMethod() {
    	Euro2PaymentURI euro2PaymentURI1 = Euro2PaymentURI.parse("euro2/payment:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?somethingyoudontunderstand=50&somethingelseyoudontget");
    	Euro2PaymentURI euro2PaymentURI2 = Euro2PaymentURI.parse("bitcoinX:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?/paymentsomethingyoudontunderstand=50");
    	Euro2PaymentURI euro2PaymentURI3 = Euro2PaymentURI.parse("bitcoin175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?somethingyoudontunderstand=50");
    	Euro2PaymentURI euro2PaymentURI4 = Euro2PaymentURI.parse("euro2:?somethingyoudontunderstand=50");
    	Euro2PaymentURI euro2PaymentURI5 = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W?label");
		Euro2PaymentURI euro2PaymentURI6 = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/somethingelsethanpayment?somethingyoudontunderstand=50&somethingelseyoudontget=999&r=https%3A%2F%2Ffoo.com%2Fi%2F7BpFbVsnh5PUisfh&req-app=appname");

    	assertNull(euro2PaymentURI1);
    	assertNull(euro2PaymentURI2);
    	assertNull(euro2PaymentURI3);
    	assertNull(euro2PaymentURI4);
    	assertNull(euro2PaymentURI5);
		assertNull(euro2PaymentURI6);
    }

    @Test
    public void testBuilder() {
    	Euro2PaymentURI euro2PaymentURI = new Euro2PaymentURI.Builder()
    		.address("175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W")
    		.amount(50.0)
    		.message("Donation for project xyz")
            .payer("PAYERSADDRESS")
            .signature("SIGNATURE")
    		.parameter("foo", "bar")
    		.requiredParameter("fiz", "biz")
    		.build();

    	assertEquals(euro2PaymentURI.getAddress(), "175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W");
    	assertEquals(euro2PaymentURI.getAmount(), Double.valueOf(50));
    	assertEquals(euro2PaymentURI.getMessage(), "Donation for project xyz");
    	assertEquals(euro2PaymentURI.getParameters().size(), 2);
    	assertEquals(euro2PaymentURI.getParameters().get("foo").getValue(), "bar");
    	assertFalse(euro2PaymentURI.getParameters().get("foo").isRequired());
    	assertEquals(euro2PaymentURI.getParameters().get("fiz").getValue(), "biz");
    	assertTrue(euro2PaymentURI.getParameters().get("fiz").isRequired());
    	assertEquals(euro2PaymentURI.getURI(), "euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=50.0&signature=SIGNATURE&req-fiz=biz&foo=bar&message=Donation%20for%20project%20xyz&payer=PAYERSADDRESS");
    }

}
