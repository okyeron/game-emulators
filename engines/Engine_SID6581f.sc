// CroneEngine_SID6581f
// SID6581f
// Revised August 2023 - steven noreyko

//
/*
freqLo0	
	bits 7-0	frequency low (8bits 0-255)

freqHi0	
	bits 7-0	frequency high (8bits 0-255)

pwLo0	
	bits 7-0	pulse width low (8bits 0-255)

pwHi0	
	bits 3-0	pulse width low (4bits 0-15)
ctrl0	
	bit 7	noise
	bit 6	square
	bit 5	saw
	bit 4	triangle
	bit 3	test
	bit 2	ring modulation
	bit 1	sync
	bit 0	gate
atkDcy0	
	bits 7-4	attack
	bits 3-0	decay (4bits 0-15)

susRel0	
	bits 7-4	sustain
	bits 3-0	release (4bits 0-15)

freqLo1	
	bits 7-0	frequency low (8bits 0-255)

freqHi1	
	bits 7-0	frequency high (8bits 0-255)

pwLo1	
	bits 7-0	pulse width low (8bits 0-255)

pwHi1	
	bits 3-0	pulse width low (4bits 0-15)

ctrl1	
	bit 7	noise
	bit 6	square
	bit 5	saw
	bit 4	triangle
	bit 3	test
	bit 2	ring modulation
	bit 1	sync
	bit 0	gate

atkDcy1	
	bits 7-4	attack
	bits 3-0	decay (4bits 0-15)

susRel1	
	bits 7-4	sustain
	bits 3-0	release (4bits 0-15)

freqLo2	
	bits 7-0	frequency low (8bits 0-255)

freqHi2	
	bits 7-0	frequency high (8bits 0-255)

pwLo2	
	bits 7-0	pulse width low (8bits 0-255)

pwHi2	
	bits 3-0	pulse width low (4bits 0-15)

ctrl2	
	bit 7	noise
	bit 6	square
	bit 5	saw
	bit 4	triangle
	bit 3	test
	bit 2	ring modulation
	bit 1	sync
	bit 0	gate

atkDcy2	
	bits 7-4	attack
	bits 3-0	decay (4bits 0-15)

susRel2	
	bits 7-4	sustain
	bits 3-0	release (4bits 0-15)

fcLo	
	bits 2-0	filter low (3bits 0-7)

fcHi	
	bits 7-0	filter high (8bits 0-255)

resFilt	
	bits 7-4	filter resonance
	bit 3	filter enable for external input (not used)
	bits 2-0	filter enable for voices 1-3 (3bits 0-7)

modeVol	
	bit 7	voice 3 off
	bit 6	highpass
	bit 5	bandpass
	bit 4	lowpass
	bits 3-0	volume (4bits 0-15)

rate	
	Floating-point playback rate. Only set when the synth is created (like .ir).


*/
Engine_SID6581f : CroneEngine {
	var pg;
 
	var gate= 1;

	var freqLo0= 0;
	var freqHi0= 0;
	var pwLo0= 	0;
	var pwHi0= 	0;
	var ctrl0= 	65;	// 2r00110101
	var atkDcy0= 0;
	var susRel0= 0;

	var freqLo1= 0;
	var freqHi1= 0;
	var pwLo1= 	0;
	var pwHi1= 	0;
	var ctrl1= 	0;	// 2r00110101
	var atkDcy1= 0;
	var susRel1= 0;

	var freqLo2= 0;
	var freqHi2= 0;
	var pwLo2= 	0;
	var pwHi2= 	0;
	var ctrl2= 	0;	// 2r00110101
	var atkDcy2= 0;
	var susRel2= 0;

	var fcLo= 0;
	var fcHi= 0;
	var res= 0;
	var mode= 15;
	var rate= 1;
	var amp= 1;
	var pan= 0;
	
	// Synth instance
	var sid6581;
	
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

//	SID6581f.ar(freqLo0: 0, freqHi0: 0, pwLo0: 0, pwHi0: 0, ctrl0: 0, atkDcy0: 0, susRel0: 0, 
// freqLo1: 0, freqHi1: 0, pwLo1: 0, pwHi1: 0, ctrl1: 0, atkDcy1: 0, susRel1: 0, freqLo2: 0, 
// freqHi2: 0, pwLo2: 0, pwHi2: 0, ctrl2: 0, atkDcy2: 0, susRel2: 0, fcLo: 0, fcHi: 0, resFilt: 0, 
// modeVol: 0, rate: 1)

	alloc {
		pg = ParGroup.tail(context.xg);

		SynthDef("SID", {
			arg out,
			freqLo0=freqLo0, freqHi0=freqHi0, pwLo0=pwLo0, pwHi0=pwHi0, ctrl0=ctrl0, atkDcy0=atkDcy0, susRel0=susRel0, 
			freqLo1=freqLo1, freqHi1=freqHi1, pwLo1=pwLo1, pwHi1=pwHi1, ctrl1=ctrl1, atkDcy1=atkDcy1, susRel1=susRel1, 
			freqLo2=freqLo2, freqHi2=freqHi2, pwLo2=pwLo2, pwHi2=pwHi2, ctrl2=ctrl2, atkDcy2=atkDcy2, susRel2=susRel2, 
        	fcLo=fcLo, fcHi=fcHi, res=res, mode=mode, rate=rate, amp=amp, pan=pan ;

			var z;
			z = SID6581f.ar(
			freqLo0, freqHi0, pwLo0, pwHi0, ctrl0, atkDcy0, susRel0, 
			freqLo1, freqHi1, pwLo1, pwHi1, ctrl1, atkDcy1, susRel1, 
			freqLo2, freqHi2, pwLo2, pwHi2, ctrl2, atkDcy2, susRel2, 
        	fcLo, fcHi, res, mode, rate);
			Out.ar(out, Pan2.ar(z*amp, pan));
		}).add;


		Server.default.sync;

		sid6581 = Synth("SID", target:pg);
		sid6581.set(
			\freqLo0, freqLo0,
			\freqHi0, freqHi0,
			\pwLo0, pwLo0,
			\pwHi0, pwHi0,
			\ctrl0, ctrl0,
			\atkDcy0, atkDcy0,
			\susRel0, susRel0,
			\freqLo1, freqLo1,
			\freqHi1, freqHi1,
			\pwLo1, pwLo1,
			\pwHi1, pwHi1,
			\ctrl1, ctrl1,
			\atkDcy1, atkDcy1,
			\susRel1, susRel1,
			\freqLo2, freqLo2,
			\freqHi2, freqHi2,
			\pwLo2, pwLo2,
			\pwHi2, pwHi2,
			\ctrl2, ctrl2,
			\atkDcy2, atkDcy2,
			\susRel2, susRel2,
			\fcLo, fcLo,
			\fcHi, fcHi,
			\res, res,
			\mode, mode,
			\rate, rate,
			\amp, amp,
			\pan, pan
		);


		this.addCommand("freqLo0", "f", { arg msg;
			freqLo0 = msg[1];
			freqLo0.postln;
			sid6581.set(\freqLo0, freqLo0);
		});

		this.addCommand("freqHi0", "f", { arg msg;
			freqHi0 = msg[1];
			freqHi0.postln;
			sid6581.set(\freqHi0, freqHi0);
		});

		this.addCommand("pwLo0", "f", { arg msg;
			pwLo0 = msg[1];
			sid6581.set(\pwLo0, pwLo0);
		});

		this.addCommand("pwHi0", "f", { arg msg;
			pwHi0 = msg[1];
			sid6581.set(\pwHi0, pwHi0);
		});

		this.addCommand("ctrl0", "i", { arg msg;
			ctrl0 = msg[1];
			ctrl0.postln;
			sid6581.set(\ctrl0, ctrl0);
		});

		this.addCommand("atkDcy0", "f", { arg msg;
			atkDcy0 = msg[1];
			sid6581.set(\atkDcy0, atkDcy0);
		});

		this.addCommand("susRel0", "f", { arg msg;
			susRel0 = msg[1];
			sid6581.set(\susRel0, susRel0);
		});

		this.addCommand("freqLo1", "f", { arg msg;
			freqLo1 = msg[1];
			sid6581.set(\freqLo1, freqLo1);
		});

		this.addCommand("freqHi1", "f", { arg msg;
			freqHi1 = msg[1];
			sid6581.set(\freqHi1, freqHi1);
		});

		this.addCommand("pwLo1", "f", { arg msg;
			pwLo1 = msg[1];
			sid6581.set(\pwLo1, pwLo1);
		});

		this.addCommand("pwHi1", "f", { arg msg;
			pwHi1 = msg[1];
			sid6581.set(\pwHi1, pwHi1);
		});

		this.addCommand("ctrl1", "f", { arg msg;
			ctrl1 = msg[1];
			sid6581.set(\ctrl1, ctrl1);
		});

		this.addCommand("atkDcy1", "f", { arg msg;
			atkDcy1 = msg[1];
			sid6581.set(\atkDcy1, atkDcy1);
		});

		this.addCommand("susRel1", "f", { arg msg;
			susRel1 = msg[1];
			sid6581.set(\susRel1, susRel1);
		});

		this.addCommand("freqLo2", "f", { arg msg;
			freqLo2 = msg[1];
			sid6581.set(\freqLo2, freqLo2);
		});

		this.addCommand("freqHi2", "f", { arg msg;
			freqHi2 = msg[1];
			sid6581.set(\freqHi2, freqHi2);
		});

		this.addCommand("pwLo2", "f", { arg msg;
			pwLo2 = msg[1];
			sid6581.set(\pwLo2, pwLo2);
		});

		this.addCommand("pwHi2", "f", { arg msg;
			pwHi2 = msg[1];
			sid6581.set(\pwHi2, pwHi2);
		});

		this.addCommand("ctrl2", "f", { arg msg;
			ctrl2 = msg[1];
			sid6581.set(\ctrl2, ctrl2);
		});

		this.addCommand("atkDcy2", "f", { arg msg;
			atkDcy2 = msg[1];
			sid6581.set(\atkDcy2, atkDcy2);
		});

		this.addCommand("susRel2", "f", { arg msg;
			susRel2 = msg[1];
			sid6581.set(\susRel2, susRel2);
		});

		this.addCommand("fcLo", "f", { arg msg;
			fcLo = msg[1];
			sid6581.set(\fcLo, fcLo);
		});

		this.addCommand("fcHi", "f", { arg msg;
			fcHi = msg[1];
			sid6581.set(\fcHi, fcHi);
		});

		this.addCommand("res", "f", { arg msg;
			res = msg[1];
			sid6581.set(\res, res);
		});

		this.addCommand("mode", "f", { arg msg;
			mode = msg[1];
			sid6581.set(\mode, mode);
		});

		this.addCommand("rate", "f", { arg msg;
			rate = msg[1];
			sid6581.set(\rate, rate);
		});

		this.addCommand("amp", "f", { arg msg;
			amp = msg[1];
			sid6581.set(\amp, amp);
		});

		this.addCommand("pan", "f", { arg msg;
			pan = msg[1];
			sid6581.set(\pan, pan);
		});


	}
	free {
		sid6581.free;
	}
}
