// package counter

// import scala.reflect.runtime.{universe => ru}
// import chisel3._
// import chiseltest._
// import chiseltest.formal._
// import chiseltest.formal.svaAnno._
// import chisel3.experimental.{ChiselAnnotation,annotate,RunFirrtlTransform}
// import firrtl.annotations.{Annotation, ReferenceTarget, SingleTargetAnnotation, Target,MultiTargetAnnotation}
// import scala.language.reflectiveCalls

// class Counter(width: Int) extends Module {
//   val io = IO(new Bundle {
//     //val en   = Input(Bool())
//     val dout = Output(UInt(width.W))
//   })
//   val countReg = RegInit(0.U(width.W))
//   val ccountReg = RegInit(0.U(width.W))
//   //val testReg = RegInit(0.U(width.W))
//   //val vis_countReg = countReg(0)
//   //val vis_testReg = testReg(2)
//   countReg := countReg + 1.U
//   // println(countReg(0))
//   // println(countReg)
//   // val temp = this.getClass.getMethod("countReg").invoke(this)
//   // val l = temp.getClass.toString
//   // println(l)
//   // println(temp.asInstanceOf[UInt] == countReg)


//   // val nname = "countReg"

//   // def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]
//   // svaAnno.makeSVAAnno(this,getTypeTag(this),nname,this.reset)

//   // val m = ru.runtimeMirror(this.getClass.getClassLoader)
//   //       val t = ru.typeOf(getTypeTag(this)).decl(ru.TermName(nname)).asTerm
//   //       val im = m.reflect(this)
//   //       val mmirror = im.reflectField(t)
//   //       val ttemp = mmirror.get
//   //       val hh = ttemp.asInstanceOf[UInt](0)
//   //       //println(hh)
//   //       annotate(new ChiselAnnotation {
//   //     // Conversion to FIRRTL Annotation 
//   //       override def toFirrtl: Annotation = 
//   //       {
//   //         new svaAnno(Seq(Seq(AtmPropAnno(hh.toTarget)):+Seq(ResetAnno(this.reset.toTarget))))
//   //       }
//   //     })


//   // import  scala.reflect.runtime.{universe => ru}
//   // val m = ru.runtimeMirror(this.getClass.getClassLoader)

//   // val t = ru.typeOf[Counter].decl(ru.TermName(nname)).asTerm
//   // val im = m.reflect(this)
//   // val mmirror = im.reflectField(t)
//   // val ttemp = mmirror.get
//   // val hh = ttemp.asInstanceOf[UInt](0)
//   // println(hh)



//   import chisel3.internal._
//   import scala.reflect.macros.blackbox
//   import scala.reflect.macros.blackbox.Context
//   import scala.language.experimental.macros
//   import scala.reflect.macros.Universe
  
//   val bs = countReg(0)
//   val ts = countReg(3)
//   assert(countReg(0))
//   svaAssert(this,"bs |-> ##[0:8] ts") 
//   svaAssert(this, "G (bs ##[1:$] ts)[*2:3] U (X (bs |-> F ts))")
//   // svaAnno.makeSVAAnno(this,this.reset,"countReg")

//   // svaAnno.makeSVAAnno(this,this.reset,"countReg(0)")
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(2,3) (ap(countReg(1)) ###(1,-1) ap(countReg(1)))*(2,3))
//   // svaAnno.makeSVAAnno(this.reset, countReg(0) ###(1,-1) countReg(1))
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0)) )
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(1,-1) )
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(1,-1) *(2,3))
//   // svaAnno.makeSVAAnno(this.reset,G ap(countReg(0)) ###(1,-1) *(2,3))
//   // svaAnno.makeSVAAnno(this.reset,G ap(countReg(0)) ###(1,-1) *(2,3) U X ap(countReg(0)) ###(1,-1) |-> F ap(countReg(2)))
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0)) ###(1,-1) |-> F ap(countReg(2)))
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0)) U ap(countReg(1)) U ap(countReg(0)) )
//   // assume(reset==false)
//   // svaAnno.makeSVAAnno(this.reset, |- ap(countReg(0)) -|)
//   // svaAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) -| -|)
//   // svaAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) ###(1,-1) -| *(2,3) -| ###(2,3) ap(countReg(1)))
//   // svaAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) ###(1,-1) -| *(2,3) -| ###(2,3) ap(countReg(1)) U X ap(countReg(2)) *(4,5) |-> G ap(countReg(2)))
//   // svaAnno.makeSVAAnno(this.reset, |- |- ap(countReg(0)) ###(1,-1) -| *(2,3) -| U ###(2,3) ap(countReg(1)))
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0) & !countReg(2)) |-> ###(1,4) ap(countReg(2) ) )
//   // svaAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) ap(countReg(1)))
//   // svaAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) ap(countReg(1)) |-> G X ap(!countReg(0)))
//   // svaAnno.makeSVAAnno(this.reset, |- ap(!countReg(0)) ###(1,-1) ap(countReg(1)) ###(2,3) *(1,-1) |->  G X ###(1,-1) ap(countReg(1)) -|)
//   // svaAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) ap(countReg(1)) ###(2,3) *(1,-1) |-> |- G X ###(1,-1) ap(countReg(1)) -|)
//   // svaAnno.makeSVAAnno(this.reset, ap(!countReg(0)) ###(1,-1) |- ap(countReg(1)) ###(2,3) -| *(1,-1) |-> G X ap(!countReg(0)))
  
//   // svaAnno.makeSVAAnno(this.reset, F |- |- ###(2, 3) ap(countReg(0)) -| *(2,3) | ap(countReg(1)) U G ap(countReg(0)) -| )
  
//   // //svaAnno.makeSVAAnno(this.reset, |- F  ###(2, 3) ap(countReg(0)) *(2,3) -| | ap(countReg(1))  U G ap(countReg(0)) )
  
//   // svaAnno.makeSVAAnno(this.reset, G |- ###(2, 3) ###(2, 3) ap(countReg(0)) && G ap(countReg(0)) -|)
//   // svaAnno.makeSVAAnno(this.reset, F |- |- ###(2, 3) ap(countReg(0)) -| *(2,3) | ap(countReg(1)) U G ap(countReg(0)) -| )
//   // svaAnno.makeSVAAnno(this.reset, ap(countReg(0))) 
//   // printlnTSeq(###(2, 3) ###(2, 3) ap(countReg(0)) ###(2, 3) ap(countReg(0)) |-> ap(countReg(1)) |-> G F ap(countReg(0)) )
//   // makeSVAAnno(Seq(FinalOp(), Leftbraket(), Leftbraket(), NextOp(), GlobalOp(), AtmProp(countReg(0))))
//   // svaAnno.makeSVAAnno(this.reset,! ap(countReg(0)) |-> F ! G ap(countReg(0)))
//   // svaAnno.makeSVAAnno(this.reset, |- ap(!countReg(0)) -|)
//   io.dout := countReg
//   // assert(countReg(0))
//   // DoPrim(not,List(Reference(reset,UIntType(IntWidth(1)),PortKind,SourceFlow)),List(),UIntType(IntWidth(1))), StringLit(), assert_)
// }
