import { Platform, useWindowDimensions } from 'react-native';
import {
  Canvas,
  Group,
  Image,
  matchFont,
  Text,
  useImage,
} from '@shopify/react-native-skia';
import {
cancelAnimation,
Easing,
Extrapolation,
interpolate,
runOnJS,
useAnimatedReaction,
useDerivedValue,
useFrameCallback,
useSharedValue,
withSequence,
withTiming,
} from 'react-native-reanimated';
import { useEffect, useState } from 'react';
import {
  GestureHandlerRootView,
  GestureDetector,
  Gesture,
} from 'react-native-gesture-handler';

const GRAVITY = 1000;
const LIFT = -500;
const obstacleWidth = 104;
const obstacleHeight = 640;

const App = () => {
  const { width, height } = useWindowDimensions();
  const [score, setScore] = useState(0);
  const bg = useImage(require('./assets/images/bg2.png'));
  const character = useImage(require('./assets/images/pig-5.png'));
  const obstacleBottom = useImage(require('./assets/images/pipe-bottom.png'));
  const obstacleTop = useImage(require('./assets/images/pipe-top.png'));
  const base = useImage(require('./assets/images/base2.png'));
  const gameOver = useSharedValue(false);
  const obstacleX = useSharedValue(width);
  const characterY = useSharedValue(height / 3);
  const characterX = width / 4;
  const characterSpeed = useSharedValue(0);
  const obstacleGap = useSharedValue(0);
  const topObstacleY = useDerivedValue(() => obstacleGap.value - 320);
  const bottomObstacleY = useDerivedValue(() => height - 320 + obstacleGap.value);

  const obstacleSpeed = useDerivedValue(() => {
    return interpolate(score, [0,10], [1.5,2]);
  });

  const obstacles = useDerivedValue(() => [
    // bottom obstacle
    {
      x: obstacleX.value,
      y: bottomObstacleY.value,
      h: obstacleHeight,
      w: obstacleWidth,
    },
    // top obstacle
    {
      x: obstacleX.value,
      y: topObstacleY.value,
      h: obstacleHeight,
      w: obstacleWidth,
    },
  ]);

  useEffect(() => {
    moveMap();
  }, []);
  
  const moveMap = () => {
    obstacleX.value = withSequence(
      withTiming(width, { duration: 0 }),
      withTiming(-150, {
        duration: 3000 / obstacleSpeed.value,
        easing: Easing.linear,
      }),
      withTiming(width, { duration: 0 })
    );
  };
  // Scoring system
  useAnimatedReaction(
    () => obstacleX.value,
    (currentValue, previousValue) => {
      const middle = characterX;
      // change offset for the position of the next gap
      if (previousValue && currentValue < -100 && previousValue > -100) {
        obstacleGap.value = Math.random() * 400 - 200;
        cancelAnimation(obstacleX);
        runOnJS(moveMap)();
      }
      if (
        currentValue !== previousValue &&
        previousValue &&
        currentValue <= middle &&
        previousValue > middle
      ) {
        runOnJS(setScore)(score + 1);
      }
    }
  );

  const isPointCollidingWithRect = (point, rect) => {
    'worklet';
    return (
      point.x >= rect.x && 
      point.x <= rect.x + rect.w && 
      point.y >= rect.y && 
      point.y <= rect.y + rect.h 
    );
  };

  // Collision detection
  useAnimatedReaction(
    () => characterY.value,
    (currentValue, previousValue) => {
      const center = {
        x: characterX + 32,
        y: characterY.value + 24,
      };

      // Ground collision detection
      if (currentValue > height - 100 || currentValue < 0) {
        gameOver.value = true;
      }

      const isColliding = obstacles.value.some((rect) =>
        isPointCollidingWithRect(center, rect)
      );
      if (isColliding) {
        gameOver.value = true;
      }
    }
  );

  useAnimatedReaction(
    () => gameOver.value,
    (currentValue, previousValue) => {
      if (currentValue && !previousValue) {
        cancelAnimation(obstacleX);
      }
    }
  );

  useFrameCallback(({ timeSincePreviousFrame: dt }) => {
    if (!dt || gameOver.value) {
      return;
    }
    characterY.value = characterY.value + (characterSpeed.value * dt) / 1000;
    characterSpeed.value = characterSpeed.value + (GRAVITY * dt) / 1000;
  });

  const restartGame = () => {
    'worklet';
    characterY.value = height / 3;
    characterSpeed.value = 0;
    gameOver.value = false;
    obstacleX.value = width;
    runOnJS(moveMap)();
    runOnJS(setScore)(0);
  };

  const gesture = Gesture.Tap().onStart(() => {
    if (gameOver.value) {
      restartGame();
    } else {
      characterSpeed.value = LIFT;
    }
  });

  const characterTransformation = useDerivedValue(() => {
    return [
      {
        rotate: interpolate(
          characterSpeed.value,
          [-500, 500],
          [-0.5, 0.5],
          Extrapolation.CLAMP
        ),
      },
    ];
  });
  const characterOrigin = useDerivedValue(() => {
    return { x: width / 4 + 32, y: characterY.value + 24 };
  });

  const fontFamily = Platform.select({ ios: "Helvetica", default: "serif" });
const fontStyle = {
  fontFamily,
  fontSize: 50,
  fontStyle: "italic",
  fontWeight: "bold",
};
const font = matchFont(fontStyle);

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <GestureDetector gesture={gesture}>
        <Canvas style={{ width, height }}>
          <Image image={bg} width={width} height={height} fit={'cover'} />
          <Image
            image={obstacleTop}
            y={topObstacleY}
            x={obstacleX}
            width={obstacleWidth}
            height={obstacleHeight}
          />
          <Image
            image={obstacleBottom}
            y={bottomObstacleY}
            x={obstacleX}
            width={obstacleWidth}
            height={obstacleHeight}
          />
          <Image
            image={base}
            width={width}
            height={150}
            y={height - 75}
            x={0}
            fit={'cover'}
          />
          <Group transform={characterTransformation} origin={characterOrigin}>
            <Image image={character} y={characterY} x={characterX} width={64} height={48} />
          </Group>
          <Text
            x={width / 2 - 20}
            y={100}
            text={score.toString()}
            font={font}
            color={'red'}
          />
        </Canvas>
      </GestureDetector>
    </GestureHandlerRootView>
  );
};
export default App;