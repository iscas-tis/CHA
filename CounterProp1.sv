module Counter(
  input   clock,
  input   reset
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_REG_INIT
  reg [3:0] countReg; // @[src/main/scala/chisel3/counter/Counter.scala 17:25]
  wire [3:0] _countReg_T_1 = countReg + 4'h1; // @[src/main/scala/chisel3/counter/Counter.scala 22:24]
  wire  bs = countReg[0]; // @[src/main/scala/chisel3/counter/Counter.scala 70:20]
  wire  ts = countReg[3]; // @[src/main/scala/chisel3/counter/Counter.scala 71:20]
  wire  en = ~reset; // @[chiseltest/src/main/scala/chiseltest/formal/svaAnno.scala 424:14]
  wire  en_1 = ~reset; // @[chiseltest/src/main/scala/chiseltest/formal/svaAnno.scala 424:14]
  always @(posedge clock) begin
    if (reset) begin // @[src/main/scala/chisel3/counter/Counter.scala 17:25]
      countReg <= 4'h0; // @[src/main/scala/chisel3/counter/Counter.scala 17:25]
    end else begin
      countReg <= _countReg_T_1; // @[src/main/scala/chisel3/counter/Counter.scala 22:12]
    end
    `ifndef SYNTHESIS
    `ifdef PRINTF_COND
      if (`PRINTF_COND) begin
    `endif
        if (~reset & ~bs) begin
          $fwrite(32'h80000002,"Assertion failed\n    at Counter.scala:72 assert(countReg(0))\n"); // @[src/main/scala/chisel3/counter/Counter.scala 72:9]
        end
    `ifdef PRINTF_COND
      end
    `endif
    `endif // SYNTHESIS
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
  countReg = _RAND_0[3:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    //
    if (~reset) begin
      assert(bs); // @[src/main/scala/chisel3/counter/Counter.scala 72:9]
    end
    if (en_1) begin
      assert((always((bs##[1:$]ts[*2:3]))until(nexttimebs |-> (eventually(ts)))) )
    end
    if (en) begin
      assert(bs |-> (1##[0:8]ts) )
    end
  end
endmodule
module CounterProp1(
  input   clock,
  input   reset
);
  wire  comp1_clock; // @[src/main/scala/chisel3/counter/CounterFormal.scala 18:21]
  wire  comp1_reset; // @[src/main/scala/chisel3/counter/CounterFormal.scala 18:21]
  wire  en = ~reset; // @[chiseltest/src/main/scala/chiseltest/formal/svaAnno.scala 424:14]
  Counter comp1 ( // @[src/main/scala/chisel3/counter/CounterFormal.scala 18:21]
    .clock(comp1_clock),
    .reset(comp1_reset)
  );
  assign comp1_clock = clock;
  assign comp1_reset = reset;
  always @(posedge clock) begin
    if (en) begin
      assert(ts |-> (1##[1:8]ts) )
    end
  end
endmodule
