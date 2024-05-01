package org.example.rpcProtocol.response;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType responseType;
    private Object data;

    private Response(){};

    public static class Builder{
        private Response response;

        public Builder(){response = new Response();}

        public Builder responseType(ResponseType responseType){
            response.responseType = responseType;
            return this;
        }

        public Builder data(Object data){
            response.data = data;
            return this;
        }

        public Response build(){return response;}
    }

    public ResponseType getResponseType(){return responseType;}

    public void setResponseType(ResponseType responseType){this.responseType = responseType;}

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type=" + responseType +
                ", data=" + data +
                '}';
    }
}
