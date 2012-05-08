package org.modcal.model;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;

import org.modcal.output.ModelOutputWrapper;

@SOAPBinding(parameterStyle = ParameterStyle.WRAPPED, style = Style.RPC)
@WebService

public interface Hydrus1D {
	ModelOutputWrapper runModel(
			@WebParam(name = "paramNames")
					List<String> paramNames,
			@WebParam(name = "paramValues")
					List<String> paramValues) throws Exception;
}
