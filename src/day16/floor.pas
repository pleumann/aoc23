program Floor;

{$a+}
{$i /Users/joerg/Projekte/pl0/lib/files.pas}

const
  Size = 110;
  Color: Integer = 0;

var
  Map: array[0..109, 0..109] of Char;
  Seen: array[0..109, 0..109] of Byte;
  Part1, Part2: Integer;

procedure ConOut(C: Char); register;
inline (
  $4d /                 (* ld c,l           *)
  $2a / $01 / $00 /     (* ld hl,($0001)    *)
  $11 / $09 / $00 /     (* ld de, $0009     *)
  $19 /                 (* add hl,de        *)
  $e9                   (* jp (hl)          *)
);

function Max(I, J: Integer): Integer;
begin
  if I > J then Max := I else Max := J;
end;

procedure Status(I: Integer);
begin
  GotoXY(40, 24);
  TextBackground(0);
  TextColor(7);
  Write('I=', I:4, '    Part 1: ', Part1:4, '     Part 2: ', Part2:4);
end;

procedure Init;
begin
  FillChar(Seen, SizeOf(Seen), 0);
  Color := Color + 1;
  if Color = 7 then Color := 1;
  TextBackground(Color);
  if Color >= 4 then TextColor(0) else TextColor(7);
end;

function Beam(X, Y, DX, DY: Integer): Integer;
var
  Result: Integer;
  C: Char;
  Z: Integer;
  S, K: Byte;
begin
  Result := 0;

  while (X >= 0) and (X < Size) and (Y >= 0) and (Y < Size) do
  begin
    C := Map[X, Y];
    S := Seen[X, Y];

    if S = 0 then Inc(Result);

    if DX = -1 then
      K := 1
    else if DY = -1 then
      K := 2
    else if DX = 1 then
      K := 4
    else
      K := 8;

    if S and K <> 0 then Break;

    Seen[X, Y] := S or K;
    if X >= 45 then if X < 65 then if Y >= 15 then if Y < 95 then
    begin
      GotoXY(Y - 14, X - 42);
      TextBackground(Color);
      ConOut(C);
    end;

    if C = '/' then
    begin
      Z := -DY;
      DY := -DX;
      DX := Z;
    end
    else if C = '\' then
    begin
      Z := DY;
      DY := DX;
      DX := Z;
    end
    else if (C = '-') and (DX <> 0) then
    begin
      Result := Result + Beam(X, Y - 1, 0, -1);
      Result := Result + Beam(X, Y + 1, 0,  1);
      Break;
    end
    else if (C = '|') and (DY <> 0) then
    begin
      Result := Result + Beam(X - 1, Y, -1, 0);
      Result := Result + Beam(X + 1, Y,  1, 0);
      Break;
    end;

    X := X + DX;
    Y := Y + DY;
  end;

  Beam := Result;
end;

procedure Solve;
var
  T: Text;
  S: string;
  I, J: Integer;
begin
  Assign(T, 'input.txt');
  Reset(T);
  for I := 0 to Size - 1 do
  begin
    ReadLine(T, S);
    Move(S[1], Map[I], Size);
  end;

  for I := 0 to 19 do
  begin
    GotoXY(1, 3 + I);
    for J := 0 to 79 do
      Write(Map[15 + J, 45 + I]);
  end;

  Status(0);

  Init;
  Part1 := Beam(I, 0, 0, 1);

  for I := 0 to Size - 1 do
  begin
    Status(I + 1);
 
    Init;
    Part2 := Max(Part2, Beam(I, 0, 0, 1));

    Init;
    Part2 := Max(Part2, Beam(I, Size -1, 0, -1));

    Init;
    Part2 := Max(Part2, Beam(0, I, 1, 0));

    Init;
    Part2 := Max(Part2, Beam(Size - 1, I, -1, 0));

    Status(I + 1);
  end;
end;
    
begin
  Write(#27'f');

  ClrScr;
  TextColor(7);
  WriteLn('*** AoC 2023.16 The Floor Will Be Lava ***');
  WriteLn;

  Part1 := 0;
  Part2 := 0;

  Solve;

  GotoXY(1, 23);
  Write(#27'e');
end.