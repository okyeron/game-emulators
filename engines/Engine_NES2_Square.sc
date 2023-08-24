// CroneEngine_NES2
// NES2

// Emulation of the sound generation hardware of the NES APU chip by Matthew Conte. 
// This UGen has 5 oscillators: 2 squares, 1 triangle, 1 noise, 1 dmc.
// References:
// http://nesdev.com/apu_ref.txt
// http://web.textfiles.com/games/nessound.txt
// http://nesdev.com/dmc.txt

/*
trig		Control or audio rate trigger.
dutycycle	Type (0-3).
loopenv		Loop envelope off or on (0/1).
envdecay	Envelope decay off or on (0/1).
vol			Volume (0-15).
sweep		Off or on (0/1).
sweeplen	Sweeplength (0-7).
sweepdir	Sweepdirection decrease or increase (0/1).
sweepshi	Sweepshift (0-7).
freq		Frequency (0-2047).
vbl	 		Length counter (0-31).
*/

Engine_NES2 : CroneEngine {
	// Nes2Square.ar(trig: 0, dutycycle: 0, loopenv: 0, envdecay: 0, vol: 10, sweep: 0, sweeplen: 0, sweepdir: 0, sweepshi: 0, freq: 100, vbl: 0)
	// Nes2Triangle.ar(trig: 0, start: 0, counter: 10, freq: 100, vbl: 0)
	// Nes2Noise.ar(trig: 0, loopenv: 0, envdecay: 0, vol: 10, short: 0, freq: 10, vbl: 0)
	// Nes2DMC.ar(trig: 0, loop: 0, freq: 1)
		
	var <synthSQ;
	var <synthTRI;
	var trig_a;
	var trig_b;
	var trig_c;
		
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		trig_a = Bus.control(context.server, 1);
		trig_b = Bus.control(context.server, 1);
		trig_c = Bus.control(context.server, 1);
		
		// Define the synth variable, whichis a function
		synthSQ = {
			// define arguments to the function
			arg out, dutycycle=0, loopenv=0, envdecay=0, vol=10, sweep=0, sweeplen=0, sweepdir=0, sweepshi=0, freqsq=100, vblsq=0, pan=0 ;
			var z;
			z = Nes2Square.ar(InTrig.kr(trig_a), dutycycle, loopenv, envdecay, vol, sweep, sweeplen, sweepdir, sweepshi, freqsq, vblsq);
			Out.ar(out, Pan2.ar(z, pan));
		}.play(args: [\out, context.out_b], target: context.xg);

		synthTRI = {
			// define arguments to the function
			arg out, start=0, counter=0, freqtri=100, vbltri=0, pan=0 ;
			var z;
			z = Nes2Triangle.ar(InTrig.kr(trig_b), start, counter, freqtri, vbltri);
			Out.ar(out, Pan2.ar(z, pan));
		}.play(args: [\out, context.out_b], target: context.xg);

			
		this.addCommand("bangSq", "", { arg msg;
			trig_a.set(1);
		});
		this.addCommand("bangTri", "", { arg msg;
			trig_b.set(1);
		});
		this.addCommand("bangNz", "", { arg msg;
			trig_c.set(1);
		});
		this.addCommand("onOff", "f", { arg msg;
			trig_a.set(msg[1]);
		});


		this.addCommand("dutycycle", "f", { arg msg;
			synthSQ.set(\dutycycle, msg[1]);
		});
		this.addCommand("loopenv", "f", { arg msg;
			synthSQ.set(\loopenv, msg[1]);
		});
		this.addCommand("envdecay", "f", { arg msg;
			synthSQ.set(\envdecay, msg[1]);
		});
		this.addCommand("vol", "f", { arg msg;
			synthSQ.set(\vol, msg[1]);
		});
		this.addCommand("sweep", "f", { arg msg;
			synthSQ.set(\sweeplen, msg[1]);
		});
		this.addCommand("sweeplen", "f", { arg msg;
			synthSQ.set(\sweeplen, msg[1]);
		});
		this.addCommand("sweepdir", "f", { arg msg;
			synthSQ.set(\sweepdir, msg[1]);
		});
		this.addCommand("sweepshi", "f", { arg msg;
			synthSQ.set(\sweepshi, msg[1]);
		});
		this.addCommand("freqsq", "f", { arg msg;
			synthSQ.set(\freqsq, msg[1]);
		});
		this.addCommand("vblsq", "i", { arg msg;
			synthSQ.set(\vblsq, msg[1]);
		});

		this.addCommand("start", "f", { arg msg;
			synthTRI.set(\start, msg[1]);
		});
		this.addCommand("counter", "f", { arg msg;
			synthTRI.set(\counter, msg[1]);
		});
		this.addCommand("freqtri", "f", { arg msg;
			synthTRI.set(\freqtri, msg[1]);
		});
		this.addCommand("vbltri", "i", { arg msg;
			synthTRI.set(\vbltri, msg[1]);
		});

	}
	// define a function that is called when the synth is shut down
	free {
		synthSQ.free;
		synthTRI.free;
	}
}
