### Euro2PaymentURI

# Gradle Dependency

## Repository

First, add the following to your app's `build.gradle` file:

```Gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

Them include the openalpr-android dependency:

```gradle
dependencies {

    // ... other dependencies here.    	
    compile "com.github.cryptofiat:euro2-payment-uri:2.0"
}
```

# Usage

## Code

Parse the URI `euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=50&message=Donation%20for%20project%20xyz&payer=XXX333PAYERADDRESS&signature=REQUESTCREATORS_SIGNATURE`.

```Java
Euro2PaymentURI euro2PaymentURI = Euro2PaymentURI.parse("euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=50&message=Donation%20for%20project%20xyz&payer=XXX333PAYERADDRESS&signature=REQUESTCREATORS_SIGNATURE");

euro2PaymentURI.getAddress(); \\ 175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W
euro2PaymentURI.getAmount(); \\ 50
euro2PaymentURI.getMessage(); \\ "Donation for project xyz"
euro2PaymentURI.getPayer(); \\"XXX333PAYERADDRESS"
euro2PaymentURI.getSignature(); \\"REQUESTCREATORS_SIGNATURE"
euro2PaymentURI.getParameters().size(); \\ 0
```

Generate the following URI `euro2:175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W/payment?amount=50.0&signature=SIGNATURE&req-fiz=biz&foo=bar&message=Donation%20for%20project%20xyz&payer=PAYERSADDRESS`

```Java
Euro2PaymentURI euro2PaymentURI = new Euro2PaymentURI.Builder()
    		.address("175tWpb8K1S7NmH4Zx6rewF9WQrcZv245W")
    		.amount(50.0)
    		.message("Donation for project xyz")
            .payer("PAYERSADDRESS")
            .signature("SIGNATURE")
    		.parameter("foo", "bar")
    		.requiredParameter("fiz", "biz")
    		.build();
```
