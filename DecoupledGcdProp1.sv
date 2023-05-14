module DecoupledGcdProp1(
  input        clock,
  input        reset,
  output       input_ready, // @[src/test/scala/testSVA/GCD/GCD.scala 25:17]
  input        input_valid, // @[src/test/scala/testSVA/GCD/GCD.scala 25:17]
  input  [3:0] input_bits_value1, // @[src/test/scala/testSVA/GCD/GCD.scala 25:17]
  input  [3:0] input_bits_value2, // @[src/test/scala/testSVA/GCD/GCD.scala 25:17]
  input        output_ready, // @[src/test/scala/testSVA/GCD/GCD.scala 26:18]
  output       output_valid, // @[src/test/scala/testSVA/GCD/GCD.scala 26:18]
  output [3:0] output_bits_value1, // @[src/test/scala/testSVA/GCD/GCD.scala 26:18]
  output [3:0] output_bits_value2, // @[src/test/scala/testSVA/GCD/GCD.scala 26:18]
  output [3:0] output_bits_gcd // @[src/test/scala/testSVA/GCD/GCD.scala 26:18]
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
  reg [31:0] _RAND_4;
  reg [31:0] _RAND_5;
`endif // RANDOMIZE_REG_INIT
  reg [3:0] xInitial; // @[src/test/scala/testSVA/GCD/GCD.scala 28:24]
  reg [3:0] yInitial; // @[src/test/scala/testSVA/GCD/GCD.scala 29:24]
  reg [3:0] x; // @[src/test/scala/testSVA/GCD/GCD.scala 30:24]
  reg [3:0] y; // @[src/test/scala/testSVA/GCD/GCD.scala 31:24]
  reg  busy; // @[src/test/scala/testSVA/GCD/GCD.scala 32:28]
  reg  resultValid; // @[src/test/scala/testSVA/GCD/GCD.scala 33:28]
  wire [3:0] _x_T_1 = x - y; // @[src/test/scala/testSVA/GCD/GCD.scala 47:14]
  wire [3:0] _y_T_1 = y - x; // @[src/test/scala/testSVA/GCD/GCD.scala 49:14]
  wire  _T_1 = x == 4'h0; // @[src/test/scala/testSVA/GCD/GCD.scala 51:12]
  wire  _GEN_9 = input_valid | ~busy; // @[src/test/scala/testSVA/GCD/GCD.scala 68:23 src/main/scala/chisel3/util/Decoupled.scala 83:20 src/test/scala/testSVA/GCD/GCD.scala 35:15]
  wire  _GEN_14 = input_valid | busy; // @[src/test/scala/testSVA/GCD/GCD.scala 68:23 74:12 32:28]
  wire  nBusy1 = ~busy; // @[src/test/scala/testSVA/GCD/GCDFormalSpec.scala 11:16]
  wire  en = ~reset; // @[chiseltest/src/main/scala/chiseltest/formal/svaAnno.scala 429:14]
  assign input_ready = busy ? ~busy : _GEN_9; // @[src/test/scala/testSVA/GCD/GCD.scala 35:15 45:15]
  assign output_valid = resultValid; // @[src/test/scala/testSVA/GCD/GCD.scala 36:16]
  assign output_bits_value1 = xInitial; // @[src/test/scala/testSVA/GCD/GCD.scala 51:34 58:26]
  assign output_bits_value2 = yInitial; // @[src/test/scala/testSVA/GCD/GCD.scala 51:34 59:26]
  assign output_bits_gcd = _T_1 ? y : x; // @[src/test/scala/testSVA/GCD/GCD.scala 52:23 53:25 55:25]
  always @(posedge clock) begin
    if (!(busy)) begin // @[src/test/scala/testSVA/GCD/GCD.scala 45:15]
      if (input_valid) begin // @[src/test/scala/testSVA/GCD/GCD.scala 68:23]
        xInitial <= input_bits_value1; // @[src/test/scala/testSVA/GCD/GCD.scala 72:16]
      end
    end
    if (!(busy)) begin // @[src/test/scala/testSVA/GCD/GCD.scala 45:15]
      if (input_valid) begin // @[src/test/scala/testSVA/GCD/GCD.scala 68:23]
        yInitial <= input_bits_value2; // @[src/test/scala/testSVA/GCD/GCD.scala 73:16]
      end
    end
    if (busy) begin // @[src/test/scala/testSVA/GCD/GCD.scala 45:15]
      if (x >= y) begin // @[src/test/scala/testSVA/GCD/GCD.scala 46:18]
        x <= _x_T_1; // @[src/test/scala/testSVA/GCD/GCD.scala 47:9]
      end
    end else if (input_valid) begin // @[src/test/scala/testSVA/GCD/GCD.scala 68:23]
      x <= input_bits_value1; // @[src/test/scala/testSVA/GCD/GCD.scala 70:9]
    end
    if (busy) begin // @[src/test/scala/testSVA/GCD/GCD.scala 45:15]
      if (!(x >= y)) begin // @[src/test/scala/testSVA/GCD/GCD.scala 46:18]
        y <= _y_T_1; // @[src/test/scala/testSVA/GCD/GCD.scala 49:9]
      end
    end else if (input_valid) begin // @[src/test/scala/testSVA/GCD/GCD.scala 68:23]
      y <= input_bits_value2; // @[src/test/scala/testSVA/GCD/GCD.scala 71:9]
    end
    if (reset) begin // @[src/test/scala/testSVA/GCD/GCD.scala 32:28]
      busy <= 1'h0; // @[src/test/scala/testSVA/GCD/GCD.scala 32:28]
    end else if (busy) begin // @[src/test/scala/testSVA/GCD/GCD.scala 45:15]
      if (x == 4'h0 | y == 4'h0) begin // @[src/test/scala/testSVA/GCD/GCD.scala 51:34]
        busy <= 1'h0; // @[src/test/scala/testSVA/GCD/GCD.scala 61:12]
      end
    end else begin
      busy <= _GEN_14;
    end
    if (reset) begin // @[src/test/scala/testSVA/GCD/GCD.scala 33:28]
      resultValid <= 1'h0; // @[src/test/scala/testSVA/GCD/GCD.scala 33:28]
    end else if (busy) begin // @[src/test/scala/testSVA/GCD/GCD.scala 45:15]
      if (x == 4'h0 | y == 4'h0) begin // @[src/test/scala/testSVA/GCD/GCD.scala 51:34]
        if (resultValid) begin // @[src/test/scala/testSVA/GCD/GCD.scala 63:25]
          resultValid <= 1'h0; // @[src/test/scala/testSVA/GCD/GCD.scala 64:21]
        end else begin
          resultValid <= 1'h1; // @[src/test/scala/testSVA/GCD/GCD.scala 60:19]
        end
      end
    end
  end
// Register and memory initialization
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  xInitial = _RAND_0[3:0];
  _RAND_1 = {1{`RANDOM}};
  yInitial = _RAND_1[3:0];
  _RAND_2 = {1{`RANDOM}};
  x = _RAND_2[3:0];
  _RAND_3 = {1{`RANDOM}};
  y = _RAND_3[3:0];
  _RAND_4 = {1{`RANDOM}};
  busy = _RAND_4[0:0];
  _RAND_5 = {1{`RANDOM}};
  resultValid = _RAND_5[0:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (en) begin
      assert(busy |-> (eventually(nBusy1)) )
    end
  end
endmodule
