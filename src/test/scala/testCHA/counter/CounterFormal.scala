// package counter

// import chisel3._
// import chiseltest.formal._
// import chiseltest.formal.chaAnno._
// import chisel3.experimental.{ChiselAnnotation,annotate}
// import firrtl.annotations.{Annotation, ReferenceTarget, SingleTargetAnnotation, Target}

// /*class CounterProp2(width: Int) extends Counter(width) {
//   //val tempp = mem.read(2.U(32.W))
//   //assert(tempp(3))
//   //assert("ok?")
//   assert(countReg === past((countReg + 1.U)(width - 1, 0),1))
// }*/

// class CounterProp1(width: Int) extends Module {
//   //val tempp = mem.read(2.U(32.W))
//   val comp1 = Module(new Counter(width))
//   val comp2 = Module(new Counter(width))
//   //val comp3 = Module(new Counter(width))
  
//   val comp1_ts = comp1.io.ts
//   val comp2_ts = comp2.io.ts
//   // assert(comp1_ts)
//   // assert(comp2_ts)
//   chaAssert(this,"comp1_ts |-> ##[1:8] comp2_ts")
//   //assert(countReg === past((countReg + 1.U)(width - 1, 0),1))
  
//   //cross-instance?
//   //makeCHAAnno(Seq(AtmProp(comp1.vis_testReg),TimeOp(1,2),AtmProp(comp2.vis_countReg)))
  
//   // def makeCHAAnno(cha: Seq[TSeqElement]) = {
//   //   annotate(new ChiselAnnotation {
//   //     // Conversion to FIRRTL Annotation 
//   //     override def toFirrtl: Annotation = 
//   //     {
//   //       val chaanotation : Seq[Seq[TSeqElementAnno]] = cha map {
//   //         case AtmProp(ap) => Seq(AtmPropAnno(ap.toTarget))
//   //         case TimeOp(lc,hc) => Seq(TimeOpAnno(lc,hc)) } 
//   //       new CHAAnno(chaanotation)
//   //     }
//   //   })
//   // }
// }
//   /*def makeAnno(signal: UInt) = {
//     annotate(new ChiselAnnotation {
//       /** Conversion to FIRRTL Annotation */
//       override def toFirrtl: Annotation = new APAnno(signal.toTarget)
//     })
//   }
//   makeAnno(countReg(0))*/


//   /*def makeCHAAnno(cha: Seq[TSeqElement]) = {
//     annotate(new ChiselAnnotation {
//       // Conversion to FIRRTL Annotation 
//       override def toFirrtl: Annotation = 
//       {
//         val chaanotation : Seq[TSeqElementAnno] = cha map {
//           case AtmProp(ap) => AtmPropAnno(ap.toTarget)
//           case TimeOp(lc,hc) =>TimeOpAnno(lc,hc) } 
//         new CHAAnno(chaanotation)
//       }
//     })
//   }
//   makeCHAAnno(Seq(AtmProp(countReg),TimeOpAnno(1,2),AtmProp(testReg)))*/
//   //assert(TSequence(Seq(AtmProp(!tempp(3)),Implication(),TimeOp(1,8),AtmProp(countReg(3)))))
//   //assert(TSequence(Seq(TimeOp(1,3),AtmProp(!countReg(0)))))
//   /*when(past(io.en)) {
//     //assert(countReg === past((countReg + 1.U)(width - 1, 0),1))
//     //assert(TSequence(Seq(AtmProp(countReg(0)),TimeOp(2,2),Implication(),TimeOp(1,1),Implication(),TimeOp(3,3),AtmProp(countReg(3)))))
    
//     //verify(TSequence(Seq(TimeOp(1,2),AtmProp(countReg(3)))), Seq(BoundedCheck(3), BtormcEngineAnnotation))
//     //assert(TSeq(a, ##1, b)) == 
//     //assert(b & past(a, 2)) 3
//     //assert(a & past(b,-2)) 1
//     //0 1 2
//     //assert(countReg === past((countReg + 1.U)(width - 1, 0),3))
//   }*/


// object CounterFormal extends App {
//   //val a = 1 
//   //val seq1 = TSequence(Seq(AtmProp(true.asBool),TimeOp(2,3)))
//   //(new chisel3.stage.ChiselStage).emitFirrtl(new Counter(5), Array("--target-dir", "build"))
//   //(new chisel3.stage.ChiselStage).emitVerilog(new Counter(5), Array("--target-dir", "build"))
//   //(new chisel3.stage.ChiselStage).emitFirrtl(new CounterProp(5), Array("--target-dir", "build"))
// }
