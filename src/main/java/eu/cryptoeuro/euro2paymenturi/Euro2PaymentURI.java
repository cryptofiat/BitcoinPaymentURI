package eu.cryptoeuro.euro2paymenturi;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.cryptoeuro.euro2paymenturi.model.Parameter;

/**
 * Java library to handle Euro2 payment URI.
 * based on SandroMachado/BitcoinPaymentURI implementation
 */

public class Euro2PaymentURI {

	private static final String SCHEME = "euro2:";
	private static final String ACTION = "payment";
	private static final String ADDRESS_ACTION_DELIMITER = "/";
	private static final String PARAMETER_AMOUNT = "amount";
	private static final String PARAMETER_PAYER = "payer";
	private static final String PARAMETER_MESSAGE = "message";
	private static final String PARAMETER_SIGNATURE = "signature";

	private final String address;

	private final HashMap<String, Parameter> parameters;

	private Euro2PaymentURI(Builder builder) {
		this.address = builder.address;

		parameters = new HashMap<String, Parameter>();

		if (builder.amount != null) {
			parameters.put(PARAMETER_AMOUNT, new Parameter(String.valueOf(builder.amount), false));
		}

		if (builder.message != null) {
			parameters.put(PARAMETER_MESSAGE, new Parameter(builder.message, false));
		}

		if (builder.signature != null) {
			parameters.put(PARAMETER_SIGNATURE, new Parameter(builder.signature, false));
		}

		if (builder.payer != null) {
			parameters.put(PARAMETER_PAYER, new Parameter(builder.payer, false));
		}

		if (builder.otherParameters != null) {
			parameters.putAll(builder.otherParameters);
		}
	}

	/**
	 * Gets the URI Euro2 address.
	 *
	 * @return the URI Euro2 address.
	 */

	public String getAddress() {
		return address;
	}

	/**
	 * Gets the URI amount.
	 *
	 * @return the URI amount.
	 */

	public Double getAmount() {
		if (parameters.get(PARAMETER_AMOUNT) == null) {
			return null;
		}
		return Double.valueOf(parameters.get(PARAMETER_AMOUNT).getValue());
	}

	public String getPayer() {
		if (parameters.get(PARAMETER_PAYER) == null) {
			return null;
		}
		return parameters.get(PARAMETER_PAYER).getValue();
	}

	public String getSignature() {
		if (parameters.get(PARAMETER_SIGNATURE) == null) {
			return null;
		}
		return parameters.get(PARAMETER_SIGNATURE).getValue();
	}

	/**
	 * Gets the URI message.
	 *
	 * @return the URI message.
	 */

	public String getMessage() {
		if (parameters.get(PARAMETER_MESSAGE) == null) {
			return null;
		}

		return parameters.get(PARAMETER_MESSAGE).getValue();
	}

	/**
	 * Gets the URI parameters.
	 *
	 * @return the URI parameters.
	 */

	public HashMap<String, Parameter> getParameters() {
		HashMap<String, Parameter> filteredParameters = new HashMap<String, Parameter>(parameters);

		filteredParameters.remove(PARAMETER_AMOUNT);
		filteredParameters.remove(PARAMETER_MESSAGE);
		filteredParameters.remove(PARAMETER_PAYER);
		filteredParameters.remove(PARAMETER_SIGNATURE);

		return filteredParameters;
	}

	/**
	 * Gets the URI.
	 *
	 * @return a string with the URI. This string can be used to make an Euro payment or payment request.
	 */

	public String getURI() {
		String queryParameters = null;
		try {
			for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
				if (queryParameters == null) {
					if (entry.getValue().isRequired()) {
						queryParameters = String.format("req-%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));

						continue;
					}

					queryParameters = String.format("%s=%s", URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));

					continue;
				}

				if (entry.getValue().isRequired()) {
					queryParameters = String.format("%s&req-%s=%s", queryParameters, URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));

					continue;
				}

				queryParameters = String.format("%s&%s=%s", queryParameters, URLEncoder.encode(entry.getKey(), "UTF-8").replace("+", "%20"), URLEncoder.encode(entry.getValue().getValue(), "UTF-8").replace("+", "%20"));
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return null;
		}

		return String.format("%s%s" + ADDRESS_ACTION_DELIMITER + "%s%s", SCHEME, getAddress(), ACTION, queryParameters == null ? "" : String.format("?%s", queryParameters));
	}

	/**
	 * Parses a string to a Euro2 payment URI.
	 *
	 * @param string The string to be parsed.
	 *
	 * @return a Euro2 payment URI if the URI is valid, or null for an invalid string.
	 */

	public static Euro2PaymentURI parse(String string) {
		try {
			string = URLDecoder.decode(string,  "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return null;
		}

		if (string == null) {
			return null;
		}

		if (string.isEmpty()) {
			return null;
		}

		if (!string.toLowerCase().startsWith(SCHEME)) {
			return null;
		}

		String euro2PaymentURIWithoutScheme = string.replaceFirst(SCHEME, "");
        ArrayList<String> euro2PaymentURIElements = new ArrayList<>(Arrays.asList(euro2PaymentURIWithoutScheme.split("\\?")));

        if (euro2PaymentURIElements.size() != 1 && euro2PaymentURIElements.size() != 2) {
        	return null;
        }

        if (euro2PaymentURIElements.get(0).length() == 0) {
        	return null;
        }

		String[] addressAndAction = euro2PaymentURIElements.get(0).split(ADDRESS_ACTION_DELIMITER);
        String address;
        if (addressAndAction.length != 2 || addressAndAction[1].isEmpty() || !addressAndAction[1].equals(ACTION)) {
        	return null;
		} else {
        	address = addressAndAction[0];
		}

        if (euro2PaymentURIElements.size() == 1) {
        	return new Builder().address(address).build();
        }

        List<String> queryParametersList = Arrays.asList(euro2PaymentURIElements.get(1).split("&"));

        if (queryParametersList.isEmpty()) {
        	return new Builder().address(address).build();
        }

        HashMap<String, String> queryParametersFiltered = new HashMap<String, String>();

        for (String query : queryParametersList) {
        	String[] queryParameter = query.split("=");

        	try {
            	queryParametersFiltered.put(queryParameter[0], queryParameter[1]);
        	} catch (ArrayIndexOutOfBoundsException exception) {
        		exception.printStackTrace();
        		return null;
        	}
        }

        Builder euro2PaymentURIBuilder = new Builder().address(address);

        if (queryParametersFiltered.containsKey(PARAMETER_AMOUNT)) {
        	euro2PaymentURIBuilder.amount(Double.valueOf(queryParametersFiltered.get(PARAMETER_AMOUNT)));
        	queryParametersFiltered.remove(PARAMETER_AMOUNT);
        }

        if (queryParametersFiltered.containsKey(PARAMETER_MESSAGE)) {
        	euro2PaymentURIBuilder.message(queryParametersFiltered.get(PARAMETER_MESSAGE));
        	queryParametersFiltered.remove(PARAMETER_MESSAGE);
        }

		if (queryParametersFiltered.containsKey(PARAMETER_PAYER)) {
			euro2PaymentURIBuilder.payer(queryParametersFiltered.get(PARAMETER_PAYER));
			queryParametersFiltered.remove(PARAMETER_PAYER);
		}

		if (queryParametersFiltered.containsKey(PARAMETER_SIGNATURE)) {
			euro2PaymentURIBuilder.signature(queryParametersFiltered.get(PARAMETER_SIGNATURE));
			queryParametersFiltered.remove(PARAMETER_SIGNATURE);
		}

		for (Map.Entry<String, String> entry : queryParametersFiltered.entrySet()) {
			euro2PaymentURIBuilder.parameter(entry.getKey(), entry.getValue());
		}

		return euro2PaymentURIBuilder.build();
	}

	public static class Builder{
		private String address;
		private Double amount;
		private String message;
		private String payer;
		private String signature;
		private HashMap<String, Parameter> otherParameters;

		/**
		 * Returns a builder for the Euro2 payment URI.
		 */

		public Builder() {
		}

		/**
		 * Adds the address to the builder.
		 *
		 * @param address The address.
		 *
		 * @return the builder with the address.
		 */

		public Builder address(String address) {
			this.address = address;

			return this;
		}

		/**
		 * Adds the amount to the builder.
		 *
		 * @param amount The amount.
		 *
		 * @return the builder with the amount.
		 */

		public Builder amount(Double amount) {
			this.amount = amount;

			return this;
		}

		/**
		 * Adds the message to the builder.
		 *
		 * @param message The message.
		 *
		 * @return the builder with the message.
		 */

		public Builder message(String message) {
			this.message = message;

			return this;
		}

		public Builder payer(String payer) {
			this.payer = payer;

			return this;
		}

		public Builder signature(String signature) {
			this.signature = signature;

			return this;
		}

		/**
		 * Adds a parameter to the builder.
		 *
		 * @param key The parameter.
		 * @param value The value.
		 *
		 * @return the builder with the parameter.
		 */

		public Builder parameter(String key, String value) {
			if (otherParameters == null) {
				otherParameters = new HashMap<String, Parameter>();
			}

			if (key.startsWith("req-")) {
				otherParameters.put(key.replace("req-", ""), new Parameter(value, true));

				return this;
			}

			otherParameters.put(key, new Parameter(value, false));

			return this;
		}

		/**
		 * Adds a required to the builder.
		 *
		 * @param key The key.
		 * @param value The value.
		 *
		 * @return the builder with the parameter.
		 */

		public Builder requiredParameter(String key, String value) {
			if (otherParameters == null) {
				otherParameters = new HashMap<String, Parameter>();
			}

			otherParameters.put(key, new Parameter(value, true));

			return this;
		}

		/**
		 * Builds a Euro2 payment URI.
		 *
		 * @return a Euro2 payment URI.
		 */

		public Euro2PaymentURI build() {
			return new Euro2PaymentURI(this);
		}

	}

}
