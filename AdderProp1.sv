module AdderProp1(
  input        clock,
  input        reset,
  input  [3:0] io_a, // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 7:14]
  input  [3:0] io_b, // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 7:14]
  input        io_cin, // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 7:14]
  output [3:0] io_c, // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 7:14]
  output       io_cout // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 7:14]
);
  wire [4:0] _res_T = io_a + io_b; // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 26:18]
  wire [4:0] _GEN_0 = {{4'd0}, io_cin}; // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 26:26]
  wire [4:0] res = _res_T + _GEN_0; // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 26:26]
  wire [3:0] _ai_T_1 = io_a & 4'h2; // @[src/test/scala/testSVA/chiselbook/Adder/AdderTest.scala 25:18]
  wire  ai = _ai_T_1[0]; // @[src/test/scala/testSVA/chiselbook/Adder/AdderTest.scala 25:41]
  wire [3:0] _bi_T_1 = io_b & 4'h2; // @[src/test/scala/testSVA/chiselbook/Adder/AdderTest.scala 26:18]
  wire  bi = _bi_T_1[0]; // @[src/test/scala/testSVA/chiselbook/Adder/AdderTest.scala 26:41]
  wire  en = ~reset; // @[chiseltest/src/main/scala/chiseltest/formal/svaAnno.scala 460:14]
  assign io_c = res[3:0]; // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 27:17]
  assign io_cout = res[4]; // @[src/test/scala/testSVA/chiselbook/Adder/Adders.scala 28:17]
  always @(posedge clock) begin
    if (en) begin
      assert(bi |-> (eventually(ai)) )
    end
  end
endmodule
